package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState

@Immutable
internal data class ExchangeRateSettingsState(
    val isOptedIn: SimpleCheckboxState,
    val isOptedOut: SimpleCheckboxState,
    val saveButton: ButtonState,
    val onBack: () -> Unit,
)

@Immutable
data class SimpleCheckboxState(
    val isChecked: Boolean,
    val onClick: () -> Unit,
)
