package co.electriccoin.zcash.ui.screen.integrations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletStateUseCase
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.integrations.model.IntegrationsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntegrationsViewModel(
    getVersionInfo: GetVersionInfoProvider,
    getZcashCurrency: GetZcashCurrencyProvider,
    observeWalletState: ObserveWalletStateUseCase,
    private val getTransparentAddress: GetTransparentAddressUseCase,
    private val isCoinbaseAvailable: IsCoinbaseAvailableUseCase,
) : ViewModel() {
    val backNavigationCommand = MutableSharedFlow<Unit>()
    val coinbaseNavigationCommand = MutableSharedFlow<String>()

    private val versionInfo = getVersionInfo()
    private val isDebug = versionInfo.let { it.isDebuggable && !it.isRunningUnderTestService }

    private val isEnabled =
        observeWalletState()
            .map {
                it != TopAppBarSubTitleState.Restoring
            }

    val state =
        isEnabled.map { isEnabled ->
            IntegrationsState(
                version = stringRes(R.string.integrations_version, versionInfo.versionName),
                disabledInfo = stringRes(R.string.integrations_disabled_info).takeIf { isEnabled.not() },
                onBack = ::onBack,
                items = listOfNotNull(
                    ZashiSettingsListItemState(
                        // Set the wallet currency by app build is more future-proof, although we hide it from the UI
                        // in the Testnet build
                        icon = R.drawable.ic_integrations_coinbase,
                        text = stringRes(R.string.integrations_coinbase, getZcashCurrency.getLocalizedName()),
                        subtitle =
                        stringRes(
                            R.string.integrations_coinbase_subtitle,
                            getZcashCurrency.getLocalizedName()
                        ),
                        onClick = ::onBuyWithCoinbaseClicked
                    ).takeIf { isCoinbaseAvailable() }
                ).toImmutableList()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onBuyWithCoinbaseClicked() =
        viewModelScope.launch {
            val appId = BuildConfig.ZCASH_COINBASE_APP_ID

            when {
                appId.isEmpty() && isDebug ->
                    coinbaseNavigationCommand.emit("https://www.coinbase.com") // fallback debug url

                appId.isEmpty() && isDebug -> {
                    // should not happen
                }

                appId.isNotEmpty() -> {
                    val address = getTransparentAddress().address
                    val url =
                        "https://pay.coinbase.com/buy/select-asset?appId=$appId&addresses={\"${address}\":[\"zcash\"]}"
                    coinbaseNavigationCommand.emit(url)
                }

                else -> {
                    // should not happen
                }
            }
        }
}
