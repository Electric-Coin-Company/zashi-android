package co.electriccoin.zcash.ui.common.model

import java.util.UUID

data class AddressBookContact(
    val name: String,
    val address: String,
    val id: String = UUID.randomUUID().toString(),
)
