package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.ObserveFiatCurrencyResult
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.NullableBooleanPreferenceDefault
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets.EXCHANGE_RATE_OPT_IN
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.common.wallet.RefreshLock
import co.electriccoin.zcash.ui.common.wallet.StaleLock
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface ExchangeRateRepository {
    val isExchangeRateUsdOptedIn: StateFlow<Boolean?>

    val state: StateFlow<ExchangeRateState>

    fun optInExchangeRateUsd(optIn: Boolean)

    fun dismissOptInExchangeRateUsd()

    fun refreshExchangeRateUsd()
}

class ExchangeRateRepositoryImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val navigationRouter: NavigationRouter,
) : ExchangeRateRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val isExchangeRateUsdOptedIn: StateFlow<Boolean?> =
        nullableBooleanStateFlow(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val exchangeRateUsdInternal =
        isExchangeRateUsdOptedIn
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
        channelFlow {
            combine(
                isExchangeRateUsdOptedIn,
                exchangeRateUsdInternal,
                staleExchangeRateUsdLock.state,
                refreshExchangeRateUsdLock.state,
            ) { isOptedIn, exchangeRate, isStale, isRefreshEnabled ->
                createState(isOptedIn, exchangeRate, isStale, isRefreshEnabled)
            }.distinctUntilChanged()
                .onEach {
                    Twig.info { "[USD] $it" }
                    send(it)
                }.launchIn(this)

            awaitClose {
                // do nothing
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5.seconds, 5.seconds),
            initialValue =
                createState(
                    isOptedIn = isExchangeRateUsdOptedIn.value,
                    exchangeRate = exchangeRateUsdInternal.value,
                    isStale = false,
                    isRefreshEnabled = false
                )
        )

    private fun createState(
        isOptedIn: Boolean?,
        exchangeRate: ObserveFiatCurrencyResult,
        isStale: Boolean,
        isRefreshEnabled: Boolean
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
                null ->
                    ExchangeRateState.OptIn(
                        onDismissClick = ::dismissWidgetOptInExchangeRateUsd,
                        onPrimaryClick = ::showOptInExchangeRateUsd
                    )
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
        setNullableBooleanPreference(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN, optIn)
        navigationRouter.back()
    }

    override fun dismissOptInExchangeRateUsd() {
        setNullableBooleanPreference(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN, false)
        navigationRouter.back()
    }

    private fun dismissWidgetOptInExchangeRateUsd() {
        setNullableBooleanPreference(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN, false)
    }

    private fun showOptInExchangeRateUsd() = navigationRouter.forward(EXCHANGE_RATE_OPT_IN)

    private fun nullableBooleanStateFlow(default: NullableBooleanPreferenceDefault): StateFlow<Boolean?> =
        flow {
            emitAll(default.observe(standardPreferenceProvider()))
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun setNullableBooleanPreference(
        default: NullableBooleanPreferenceDefault,
        newState: Boolean
    ) {
        scope.launch {
            default.putValue(standardPreferenceProvider(), newState)
        }
    }
}

private val USD_EXCHANGE_REFRESH_LOCK_THRESHOLD = 2.minutes
private val USD_EXCHANGE_STALE_LOCK_THRESHOLD = 15.minutes
