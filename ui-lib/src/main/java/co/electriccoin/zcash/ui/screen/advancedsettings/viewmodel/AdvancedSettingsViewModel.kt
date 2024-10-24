package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.SensitiveSettingsVisibleUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.model.AdvancedSettingsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel(
    isSensitiveSettingsVisible: SensitiveSettingsVisibleUseCase
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
                    ZashiSettingsListItemState(
                        text = stringRes(R.string.advanced_settings_recovery),
                        icon = R.drawable.ic_advanced_settings_recovery,
                        onClick = {}
                    ),
                    ZashiSettingsListItemState(
                        text = stringRes(R.string.advanced_settings_export),
                        icon = R.drawable.ic_advanced_settings_export,
                        onClick = {}
                    ),
                    ZashiSettingsListItemState(
                        text = stringRes(R.string.advanced_settings_choose_server),
                        icon =
                            R.drawable.ic_advanced_settings_choose_server,
                        onClick = ::onChooseServerClick
                    ).takeIf { isSensitiveSettingsVisible },
                    ZashiSettingsListItemState(
                        text = stringRes(R.string.advanced_settings_currency_conversion),
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

    val navigationCommand = MutableSharedFlow<String>()
    val backNavigationCommand = MutableSharedFlow<Unit>()

    private fun onChooseServerClick() =
        viewModelScope.launch {
            navigationCommand.emit(NavigationTargets.CHOOSE_SERVER)
        }

    private fun onCurrencyConversionClick() =
        viewModelScope.launch {
            navigationCommand.emit(NavigationTargets.SETTINGS_EXCHANGE_RATE_OPT_IN)
        }

    fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }
}
