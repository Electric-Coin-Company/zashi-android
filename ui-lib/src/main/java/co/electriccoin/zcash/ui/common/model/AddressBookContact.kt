package co.electriccoin.zcash.ui.common.model

import kotlinx.datetime.Instant
import java.util.UUID

data class AddressBookContact(
    val name: String,
    val address: String,
    val lastUpdated: Instant,
    val id: String = UUID.randomUUID().toString(),
)
