package co.electriccoin.zcash.ui.screen.scan.model

import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.extension.AddressTypeAsStringSerializer
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import kotlinx.serialization.Serializable

@Serializable
data class ScanResult(
    val address: String,
    @Serializable(with = AddressTypeAsStringSerializer::class)
    val type: AddressType
) {
    init {
        // Basic validation to support the class properties type-safeness
        require(address.isNotEmpty()) {
            "Address parameter $address can not be empty"
        }
    }

    fun toRecipient() = RecipientAddressState(address, type)
}
