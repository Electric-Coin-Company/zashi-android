package co.electriccoin.zcash.ui.screen.contact.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource

data class ContactState(
    val title: StringResource,
    val isLoading: Boolean,
    val walletAddress: TextFieldState,
    val contactName: TextFieldState,
    val negativeButton: ButtonState?,
    val positiveButton: ButtonState,
    val onBack: () -> Unit,
)
