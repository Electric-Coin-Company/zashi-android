package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel(
    getVersionInfo: GetVersionInfoProvider,
    getZcashCurrency: GetZcashCurrencyProvider,
    private val getTransparentAddress: GetTransparentAddressUseCase,
) : ViewModel() {
    private val forceShowCoinbaseForDebug = getVersionInfo().let { it.isDebuggable && !it.isRunningUnderTestService }

    val state =
        MutableStateFlow(
            AdvancedSettingsState(
                onBack = ::onBack,
                onRecoveryPhraseClick = {},
                onExportPrivateDataClick = {},
                onChooseServerClick = ::onChooseServerClick,
                onCurrencyConversionClick = ::onCurrencyConversionClick,
                onDeleteZashiClick = {},
                coinbaseButton =
                    ZashiSettingsListItemState(
                        // Set the wallet currency by app build is more future-proof, although we hide it from the UI
                        // in the Testnet build
                        text = stringRes(R.string.advanced_settings_coinbase, getZcashCurrency.getLocalizedName()),
                        onClick = { onBuyWithCoinbaseClicked() }
                    ).takeIf {
                        !getVersionInfo().isTestnet &&
                            (BuildConfig.ZCASH_COINBASE_APP_ID.isNotEmpty() || forceShowCoinbaseForDebug)
                    }
            )
        ).asStateFlow()

    val navigationCommand = MutableSharedFlow<String>()
    val backNavigationCommand = MutableSharedFlow<Unit>()
    val coinbaseNavigationCommand = MutableSharedFlow<String>()

    private fun onChooseServerClick() =
        viewModelScope.launch {
            navigationCommand.emit(NavigationTargets.CHOOSE_SERVER)
        }

    private fun onCurrencyConversionClick() =
        viewModelScope.launch {
            navigationCommand.emit(NavigationTargets.SETTINGS_EXCHANGE_RATE_OPT_IN)
        }

    private fun onBuyWithCoinbaseClicked() {
        viewModelScope.launch {
            val appId = BuildConfig.ZCASH_COINBASE_APP_ID

            when {
                appId.isEmpty() && forceShowCoinbaseForDebug ->
                    coinbaseNavigationCommand.emit("https://www.coinbase.com") // fallback debug url

                appId.isEmpty() && forceShowCoinbaseForDebug -> {
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

    fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }
}
