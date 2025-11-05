package co.electriccoin.zcash.ui.screen.swap.quote

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.SwapTokenAmountState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
internal sealed interface SwapQuoteState : ModalBottomSheetState {
    @Immutable
    data class Success(
        val title: StringResource,
        val rotateIcon: Boolean,
        val from: SwapTokenAmountState,
        val to: SwapTokenAmountState,
        val items: List<SwapQuoteInfoItem>,
        val amount: SwapQuoteInfoItem,
        val primaryButton: ButtonState,
        val infoText: StringResource?,
        override val onBack: () -> Unit,
    ) : SwapQuoteState

    @Immutable
    data class Error(
        val icon: ImageResource,
        val title: StringResource,
        val subtitle: StringResource,
        val negativeButton: ButtonState,
        val positiveButton: ButtonState,
        override val onBack: () -> Unit,
    ) : SwapQuoteState
}

@Immutable
data class SwapQuoteInfoItem(
    val description: StringResource,
    val title: StringResource,
    val subtitle: StringResource? = null,
)
