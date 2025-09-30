package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_SERIALIZATION_V2
import kotlinx.datetime.Instant

data class AddressBook(
    val lastUpdated: Instant,
    val version: Int = ADDRESS_BOOK_SERIALIZATION_V2,
    val contacts: List<AddressBookContact>
)
