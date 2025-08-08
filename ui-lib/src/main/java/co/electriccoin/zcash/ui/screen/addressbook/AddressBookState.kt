package co.electriccoin.zcash.ui.screen.addressbook

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.listitem.ContactListItemState
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource

data class AddressBookState(
    val info: IconButtonState?,
    val title: StringResource,
    val items: List<AddressBookItem>,
    val isLoading: Boolean,
    val onBack: () -> Unit,
    val scanButton: ButtonState,
    val manualButton: ButtonState
)

sealed interface AddressBookItem : Itemizable {
    data class Title(
        val title: StringResource
    ) : AddressBookItem {
        override val contentType = "Title"
        override val key = Empty.contentType
    }

    data class Contact(
        val state: ContactListItemState
    ) : AddressBookItem {
        override val contentType = "Contact"
        override val key = Empty.contentType
    }

    data object Empty : AddressBookItem {
        override val contentType = "Empty"
        override val key = contentType
    }
}
