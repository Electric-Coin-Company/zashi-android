package co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel : ViewModel() {
    val state: StateFlow<AdvancedSettingsState> =
        MutableStateFlow(
            AdvancedSettingsState(
                onBack = ::onBack,
                onRecoveryPhraseClick = {},
                onExportPrivateDataClick = {},
                onChooseServerClick = ::onChooseServerClick,
                onCurrencyConversionClick = ::onCurrencyConversionClick,
                onDeleteZashiClick = {}
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
