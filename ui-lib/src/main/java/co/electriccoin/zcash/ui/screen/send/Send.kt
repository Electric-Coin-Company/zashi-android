package co.electriccoin.zcash.ui.screen.send

import cash.z.ecc.sdk.model.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class Send(
    val recipientAddress: String? = null,
    val recipientAddressType: AddressType? = null,
)
