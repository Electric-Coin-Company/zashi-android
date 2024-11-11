package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.advancedsettings.model.AdvancedSettingsState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel : ViewModel() {
    val state =
        MutableStateFlow(
            AdvancedSettingsState(
                onBack = ::onBack,
                items =
                    persistentListOf(
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
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes(R.string.advanced_settings_currency_conversion),
                            icon =
                                R.drawable.ic_advanced_settings_currency_conversion,
                            onClick = ::onCurrencyConversionClick
                        )
                    ),
                deleteButton =
                    ButtonState(
                        stringRes(R.string.advanced_settings_delete_button),
                        onClick = {}
                    )
            )
        ).asStateFlow()

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
