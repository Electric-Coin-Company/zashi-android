package co.electriccoin.zcash.ui.common.model

import kotlinx.datetime.Instant

data class AddressBook(
    val lastUpdated: Instant,
    val version: Int,
    val contacts: List<AddressBookContact>
)
