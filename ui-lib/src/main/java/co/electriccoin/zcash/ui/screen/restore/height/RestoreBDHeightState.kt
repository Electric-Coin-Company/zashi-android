package co.electriccoin.zcash.ui.screen.restore.height

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState

data class RestoreBDHeightState(
    val blockHeight: TextFieldState,
    val estimate: ButtonState,
    val restore: ButtonState,
    val dialogButton: IconButtonState,
    val onBack: () -> Unit
)
