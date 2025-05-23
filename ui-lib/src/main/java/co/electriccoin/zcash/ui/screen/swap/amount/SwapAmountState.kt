package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState

@Immutable
data class SwapAmountState(
    val recipientGets: SwapTextFieldState,
    val slippage: ButtonState,
    val youPay: SwapTextState,
    val primaryButton: ButtonState,
    val onBack: () -> Unit
)
