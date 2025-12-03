package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
internal data class ExchangeRateSettingsState(
    val isOptedIn: SimpleCheckboxState,
    val isOptedOut: SimpleCheckboxState,
    val saveButton: ButtonState,
    val info: StringResource?,
    val onBack: () -> Unit,
)

@Immutable
data class SimpleCheckboxState(
    val isChecked: Boolean,
    val onClick: () -> Unit,
)
