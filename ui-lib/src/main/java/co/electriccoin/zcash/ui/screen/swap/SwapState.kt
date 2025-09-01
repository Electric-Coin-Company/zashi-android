package co.electriccoin.zcash.ui.screen.swap

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ChipButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.SimpleListItemState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.screen.swap.ui.SwapAmountTextFieldState
import co.electriccoin.zcash.ui.screen.swap.ui.SwapAmountTextState

@Immutable
internal data class SwapState(
    val appBarState: SwapAppBarState,
    val swapInfoButton: IconButtonState,
    val amountTextField: SwapAmountTextFieldState,
    val slippage: ButtonState,
    val amountText: SwapAmountTextState,
    val infoItems: List<SimpleListItemState>,
    val addressContact: ChipButtonState? = null,
    val address: TextFieldState,
    val isAddressBookHintVisible: Boolean,
    val qrScannerButton: IconButtonState,
    val addressBookButton: IconButtonState,
    val errorFooter: ErrorFooter?,
    val primaryButton: ButtonState?,
    val onBack: () -> Unit
)

@Immutable
data class ErrorFooter(
    val title: StringResource,
    val subtitle: StringResource,
)

@Immutable
internal data class SwapAppBarState(
    val title: StringResource,
    @DrawableRes val icon: Int,
)

@Immutable
internal data class SwapCancelState(
    val icon: ImageResource,
    val title: StringResource,
    val subtitle: StringResource,
    val negativeButton: ButtonState,
    val positiveButton: ButtonState,
    override val onBack: () -> Unit,
) : ModalBottomSheetState
