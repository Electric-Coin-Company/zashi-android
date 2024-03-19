package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.extension.AddressTypeAsStringSerializer
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data class SerializableAddress(
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

    internal fun toRecipient() = RecipientAddressState(address, type)

    // Calling the conversion inside the blocking coroutine is ok, as we do not expect it to be time-consuming
    internal fun toWalletAddress() =
        runBlocking {
            when (type) {
                AddressType.Unified -> WalletAddress.Unified.new(address)
                AddressType.Shielded -> WalletAddress.Sapling.new(address)
                AddressType.Transparent -> WalletAddress.Transparent.new(address)
                is AddressType.Invalid -> error("Invalid address type")
            }
        }
}
