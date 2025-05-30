package co.electriccoin.zcash.ui.screen.swap.receiver

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.PickerState
import co.electriccoin.zcash.ui.design.component.TextFieldState

@Immutable
data class SwapReceiverState(
    val address: TextFieldState,
    val chainToken: PickerState,
    val isAddressBookHintVisible: Boolean,
    val addressBookButton: IconButtonState,
    val qrScannerButton: IconButtonState,
    val positiveButton: ButtonState,
    val onBack: () -> Unit
)
