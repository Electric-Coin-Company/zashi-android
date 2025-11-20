package co.electriccoin.zcash.ui.screen.restore.height

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState

@Immutable
data class RestoreBDHeightState(
    val blockHeight: NumberTextFieldState,
    val estimate: ButtonState,
    val restore: ButtonState,
    val dialogButton: IconButtonState,
    val onBack: () -> Unit
)
