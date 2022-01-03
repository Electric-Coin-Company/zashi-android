package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.WalletAddresses

object WalletAddressesFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture
    const val VIEWING_KEY = "03feaa290589a20f795f302ba03847b0a6c9c2b571d75d80bc4ebb02382d0549da"

    suspend fun new(
        unified: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING,
        shieldedSapling: String = WalletAddressFixture.SHIELDED_SAPLING_ADDRESS_STRING,
        transparent: String = WalletAddressFixture.TRANSPARENT_ADDRESS_STRING,
        viewingKey: String = VIEWING_KEY
    ) = WalletAddresses(
        WalletAddress.Unified.new(unified),
        WalletAddress.Shielded.new(shieldedSapling),
        WalletAddress.Transparent.new(transparent),
        viewingKey
    )
}
