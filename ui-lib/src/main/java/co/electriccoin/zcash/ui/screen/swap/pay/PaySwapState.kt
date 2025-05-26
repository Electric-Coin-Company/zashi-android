package co.electriccoin.zcash.ui.screen.swap.pay

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class PaySwapState(
    val from: SwapTokenAmountState,
    val to: SwapTokenAmountState,
    val items: List<PaySwapInfoItem>,
    val amount: PaySwapInfoItem,
    val primaryButton: ButtonState,
    override val onBack: () -> Unit,
) : ModalBottomSheetState

@Immutable
data class PaySwapInfoItem(
    val description: StringResource,
    val title: StringResource,
    val subtitle: StringResource?,
)
