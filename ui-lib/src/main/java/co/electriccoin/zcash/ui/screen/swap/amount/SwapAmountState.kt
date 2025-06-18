package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.listitem.SimpleListItemState

@Immutable
data class SwapAmountState(
    val swapWidgetState: SwapWidgetState,
    val swapInfoButton: IconButtonState,
    val recipientGets: SwapTextFieldState,
    val slippage: ButtonState,
    val youPay: SwapTextState,
    val primaryButton: ButtonState,
    val infoItems: List<SimpleListItemState>,
    val onBack: () -> Unit
)
