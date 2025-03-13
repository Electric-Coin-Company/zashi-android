package co.electriccoin.zcash.ui.screen.restore.date

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState

data class RestoreBDDateState(
    val next: ButtonState,
    val dialogButton: IconButtonState,
    val onBack: () -> Unit
)
