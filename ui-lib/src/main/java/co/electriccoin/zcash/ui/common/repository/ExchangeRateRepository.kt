package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.ObserveFiatCurrencyResult
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.NullableBooleanPreferenceDefault
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationTargets.EXCHANGE_RATE_OPT_IN
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.common.wallet.RefreshLock
import co.electriccoin.zcash.ui.common.wallet.StaleLock
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

interface ExchangeRateRepository {
    val navigationCommand: MutableSharedFlow<String>

    val backNavigationCommand: MutableSharedFlow<Unit>

    val isExchangeRateUsdOptedIn: StateFlow<Boolean?>

    val state: StateFlow<ExchangeRateState>

    fun optInExchangeRateUsd(optIn: Boolean)

    fun dismissOptInExchangeRateUsd()

    fun refreshExchangeRateUsd()
}

class ExchangeRateRepositoryImpl(
    private val walletRepository: WalletRepository,
    private val standardPreferenceProvider: StandardPreferenceProvider,
) : ExchangeRateRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val isExchangeRateUsdOptedIn: StateFlow<Boolean?> =
        nullableBooleanStateFlow(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val exchangeRateUsdInternal =
        isExchangeRateUsdOptedIn.flatMapLatest { optedIn ->
            if (optedIn == true) {
                walletRepository.synchronizer
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
            }
            .distinctUntilChanged()

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

                lastExchangeRateUsdValue
            }.distinctUntilChanged()
                .onEach {
                    Twig.info { "[USD] $it" }
                    send(it)
                }
                .launchIn(this)

            awaitClose {
                // do nothing
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ExchangeRateState.OptedOut
        )

    override val navigationCommand = MutableSharedFlow<String>()

    override val backNavigationCommand = MutableSharedFlow<Unit>()

    override fun refreshExchangeRateUsd() {
        refreshExchangeRateUsdInternal()
    }

    private fun refreshExchangeRateUsdInternal() =
        scope.launch {
            val synchronizer = walletRepository.synchronizer.filterNotNull().first()
            val value = state.value
            if (value is ExchangeRateState.Data && value.isRefreshEnabled && !value.isLoading) {
                synchronizer.refreshExchangeRateUsd()
            }
        }

    override fun optInExchangeRateUsd(optIn: Boolean) {
        scope.launch {
            setNullableBooleanPreference(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN, optIn)
            backNavigationCommand.emit(Unit)
        }
    }

    override fun dismissOptInExchangeRateUsd() {
        scope.launch {
            setNullableBooleanPreference(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN, false)
            backNavigationCommand.emit(Unit)
        }
    }

    private fun dismissWidgetOptInExchangeRateUsd() {
        setNullableBooleanPreference(StandardPreferenceKeys.EXCHANGE_RATE_OPTED_IN, false)
    }

    private fun showOptInExchangeRateUsd() =
        scope.launch {
            navigationCommand.emit(EXCHANGE_RATE_OPT_IN)
        }

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
