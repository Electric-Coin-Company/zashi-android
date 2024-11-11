package co.electriccoin.zcash.ui.screen.send.model

import androidx.annotation.DrawableRes
import co.electriccoin.zcash.ui.R

data class SendAddressBookState(
    val mode: Mode,
    val isHintVisible: Boolean,
    val onButtonClick: () -> Unit
) {
    enum class Mode(
        @DrawableRes val icon: Int
    ) {
        PICK_FROM_ADDRESS_BOOK(R.drawable.send_address_book),
        ADD_TO_ADDRESS_BOOK(R.drawable.send_address_book_plus)
    }
}
