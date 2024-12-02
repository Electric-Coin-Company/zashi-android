package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.SensitiveSettingsVisibleUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.model.AdvancedSettingsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AdvancedSettingsViewModel(
    isSensitiveSettingsVisible: SensitiveSettingsVisibleUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state: StateFlow<AdvancedSettingsState> =
        isSensitiveSettingsVisible()
            .map { isSensitiveSettingsVisible ->
                createState(isSensitiveSettingsVisible)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(isSensitiveSettingsVisible().value)
            )

    private fun createState(isSensitiveSettingsVisible: Boolean) =
        AdvancedSettingsState(
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

    fun onBack() = navigationRouter.back()
}
