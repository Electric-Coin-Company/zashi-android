package co.electriccoin.zcash.ui.screen.swap

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.SimpleListItemState

@Immutable
internal data class SwapState(
    val swapModeSelectorState: SwapModeSelectorState,
    val swapInfoButton: IconButtonState,
    val amountTextField: SwapAmountTextFieldState,
    val slippage: ButtonState,
    val amountText: SwapAmountTextState,
    val primaryButton: ButtonState,
    val infoItems: List<SimpleListItemState>,
    val address: TextFieldState,
    val isAddressBookHintVisible: Boolean,
    val onBack: () -> Unit
)
