package co.electriccoin.zcash.ui.common.model

import kotlinx.datetime.Instant

data class AddressBookContact(
    val name: String,
    val address: String,
    val lastUpdated: Instant,
)
