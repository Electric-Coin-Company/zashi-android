package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.ObserveFiatCurrencyResult
import co.electriccoin.zcash.ui.common.provider.ExchangeRateOptInStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletTorProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.TorState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.common.wallet.RefreshLock
import co.electriccoin.zcash.ui.common.wallet.StaleLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface ExchangeRateRepository {
    val state: StateFlow<ExchangeRateState>

    fun optInExchangeRateUsd(optIn: Boolean)

    fun refreshExchangeRateUsd()
}

class ExchangeRateRepositoryImpl(
    persistableWalletTorProvider: PersistableWalletTorProvider,
    private val synchronizerProvider: SynchronizerProvider,
    private val exchangeRateOptInStorageProvider: ExchangeRateOptInStorageProvider
) : ExchangeRateRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val isExchangeRateOptedIn =
        combine(
            exchangeRateOptInStorageProvider.observe(),
            persistableWalletTorProvider.observe()
        ) { isOptedIn, tor ->
            if (isOptedIn == null || tor == null) {
                null
            } else {
                isOptedIn && tor !in listOf(TorState.IMPLICITLY_DISABLED, TorState.EXPLICITLY_DISABLED)
            }
        }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val exchangeRateUsdInternal =
        isExchangeRateOptedIn
            .flatMapLatest { optedIn ->
                if (optedIn == true) {
                    synchronizerProvider
                        .synchronizer
                        .filterNotNull()
                        .flatMapLatest { synchronizer ->
                            synchronizer.exchangeRateUsd
                        }
                } else {
                    flowOf(ObserveFiatCurrencyResult(isLoading = false, currencyConversion = null))
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(USD_EXCHANGE_REFRESH_LOCK_THRESHOLD),
                initialValue = ObserveFiatCurrencyResult(isLoading = false, currencyConversion = null)
            )

    private val usdExchangeRateTimestamp =
        exchangeRateUsdInternal
            .map {
                it.currencyConversion?.timestamp
            }.distinctUntilChanged()

    private val refreshExchangeRateUsdLock =
        RefreshLock(
            timestampToObserve = usdExchangeRateTimestamp,
            lockDuration = USD_EXCHANGE_REFRESH_LOCK_THRESHOLD
        )

    private val staleExchangeRateUsdLock =
        StaleLock(
            timestampToObserve = usdExchangeRateTimestamp,
            lockDuration = USD_EXCHANGE_STALE_LOCK_THRESHOLD,
            onRefresh = { refreshExchangeRateUsdInternal().join() }
        )

    private var lastExchangeRateUsdValue: ExchangeRateState = ExchangeRateState.OptedOut

    override val state: StateFlow<ExchangeRateState> =
        combine(
            isExchangeRateOptedIn,
            exchangeRateUsdInternal,
            staleExchangeRateUsdLock.state,
            refreshExchangeRateUsdLock.state,
        ) { isOptedIn, exchangeRate, isStale, isRefreshEnabled ->
            createState(isOptedIn, exchangeRate, isStale, isRefreshEnabled)
        }.distinctUntilChanged()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds, 5.seconds),
                initialValue =
                    createState(
                        isOptedIn = isExchangeRateOptedIn.value,
                        exchangeRate = exchangeRateUsdInternal.value,
                        isStale = false,
                        isRefreshEnabled = false,
                    )
            )

    private fun createState(
        isOptedIn: Boolean?,
        exchangeRate: ObserveFiatCurrencyResult,
        isStale: Boolean,
        isRefreshEnabled: Boolean,
    ): ExchangeRateState {
        lastExchangeRateUsdValue =
            when (isOptedIn) {
                true ->
                    when (val lastValue = lastExchangeRateUsdValue) {
                        is ExchangeRateState.Data ->
                            lastValue.copy(
                                isLoading = exchangeRate.isLoading,
                                isStale = isStale,
                                isRefreshEnabled = isRefreshEnabled,
                                currencyConversion = exchangeRate.currencyConversion,
                            )

                        ExchangeRateState.OptedOut ->
                            ExchangeRateState.Data(
                                isLoading = exchangeRate.isLoading,
                                isStale = isStale,
                                isRefreshEnabled = isRefreshEnabled,
                                currencyConversion = exchangeRate.currencyConversion,
                                onRefresh = ::refreshExchangeRateUsd
                            )

                        is ExchangeRateState.OptIn ->
                            ExchangeRateState.Data(
                                isLoading = exchangeRate.isLoading,
                                isStale = isStale,
                                isRefreshEnabled = isRefreshEnabled,
                                currencyConversion = exchangeRate.currencyConversion,
                                onRefresh = ::refreshExchangeRateUsd
                            )
                    }

                false -> ExchangeRateState.OptedOut
                null -> ExchangeRateState.OptIn
            }

        return lastExchangeRateUsdValue
    }

    override fun refreshExchangeRateUsd() {
        refreshExchangeRateUsdInternal()
    }

    private fun refreshExchangeRateUsdInternal() =
        scope.launch {
            val synchronizer = synchronizerProvider.getSynchronizer()
            val value = state.value
            if (value is ExchangeRateState.Data && value.isRefreshEnabled && !value.isLoading) {
                synchronizer.refreshExchangeRateUsd()
            }
        }

    override fun optInExchangeRateUsd(optIn: Boolean) {
        scope.launch { exchangeRateOptInStorageProvider.store(optIn) }
    }
}

private val USD_EXCHANGE_REFRESH_LOCK_THRESHOLD = 2.minutes
private val USD_EXCHANGE_STALE_LOCK_THRESHOLD = 15.minutes
