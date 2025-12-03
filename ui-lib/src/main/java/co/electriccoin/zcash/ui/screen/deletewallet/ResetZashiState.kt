package co.electriccoin.zcash.ui.screen.deletewallet

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState

@Immutable
data class ResetZashiState(
    val onBack: () -> Unit,
    val checkboxState: CheckboxState,
    val buttonState: ButtonState,
)
