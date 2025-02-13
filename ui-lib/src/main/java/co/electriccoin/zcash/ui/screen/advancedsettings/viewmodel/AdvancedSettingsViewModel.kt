package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.usecase.NavigateToTaxExportUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletStateUseCase
import co.electriccoin.zcash.ui.common.usecase.SensitiveSettingsVisibleUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.model.AdvancedSettingsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel(
    isSensitiveSettingsVisible: SensitiveSettingsVisibleUseCase,
    observeWalletState: ObserveWalletStateUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToTaxExport: NavigateToTaxExportUseCase
) : ViewModel() {
    val state: StateFlow<AdvancedSettingsState> =
        combine(
            isSensitiveSettingsVisible(),
            observeWalletState()
        ) { isSensitiveSettingsVisible, walletState ->
            createState(isSensitiveSettingsVisible, walletState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(isSensitiveSettingsVisible().value, observeWalletState().value)
        )

    private fun createState(
        isSensitiveSettingsVisible: Boolean,
        topAppBarSubTitleState: TopAppBarSubTitleState
    ) = AdvancedSettingsState(
        onBack = ::onBack,
        items =
            listOfNotNull(
                ZashiListItemState(
                    title = stringRes(R.string.advanced_settings_recovery),
                    icon = R.drawable.ic_advanced_settings_recovery,
                    onClick = {}
                ),
                ZashiListItemState(
                    title = stringRes(R.string.advanced_settings_export),
                    icon = R.drawable.ic_advanced_settings_export,
                    onClick = {}
                ),
                ZashiListItemState(
                    title = stringRes(R.string.advanced_settings_tax),
                    icon =
                        when (topAppBarSubTitleState) {
                            TopAppBarSubTitleState.Restoring -> R.drawable.ic_advanced_settings_tax_disabled
                            TopAppBarSubTitleState.Disconnected -> R.drawable.ic_advanced_settings_tax
                            TopAppBarSubTitleState.None -> R.drawable.ic_advanced_settings_tax
                        },
                    isEnabled =
                        when (topAppBarSubTitleState) {
                            TopAppBarSubTitleState.Restoring -> false
                            TopAppBarSubTitleState.Disconnected -> true
                            TopAppBarSubTitleState.None -> true
                        },
                    onClick = ::onTaxExportClick
                ),
                ZashiListItemState(
                    title = stringRes(R.string.advanced_settings_choose_server),
                    icon =
                        R.drawable.ic_advanced_settings_choose_server,
                    onClick = ::onChooseServerClick
                ).takeIf { isSensitiveSettingsVisible },
                ZashiListItemState(
                    title = stringRes(R.string.advanced_settings_currency_conversion),
                    icon =
                        R.drawable.ic_advanced_settings_currency_conversion,
                    onClick = ::onCurrencyConversionClick
                ).takeIf { isSensitiveSettingsVisible }
            ).toImmutableList(),
        deleteButton =
            ButtonState(
                stringRes(R.string.advanced_settings_delete_button),
                onClick = {}
            ),
    )

    private fun onChooseServerClick() = navigationRouter.forward(NavigationTargets.CHOOSE_SERVER)

    private fun onCurrencyConversionClick() = navigationRouter.forward(NavigationTargets.SETTINGS_EXCHANGE_RATE_OPT_IN)

    private fun onTaxExportClick() =
        viewModelScope.launch {
            navigateToTaxExport()
        }

    fun onBack() = navigationRouter.back()
}
