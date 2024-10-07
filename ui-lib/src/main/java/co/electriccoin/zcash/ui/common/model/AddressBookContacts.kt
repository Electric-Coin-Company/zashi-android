package co.electriccoin.zcash.ui.common.model

import kotlinx.datetime.Instant

data class AddressBookContacts(
    val lastUpdated: Instant,
    val version: Int,
    val contacts: List<AddressBookContact>
)
