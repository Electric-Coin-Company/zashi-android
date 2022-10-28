package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.type.UnifiedFullViewingKey
import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.WalletAddresses

object WalletAddressesFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture
    val VIEWING_KEY = UnifiedFullViewingKey("03feaa290589a20f795f302ba03847b0a6c9c2b571d75d80bc4ebb02382d0549da")

    suspend fun new(
        unified: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING,
        shieldedSapling: String = WalletAddressFixture.SHIELDED_SAPLING_ADDRESS_STRING,
        transparent: String = WalletAddressFixture.TRANSPARENT_ADDRESS_STRING,
        viewingKey: UnifiedFullViewingKey = VIEWING_KEY
    ) = WalletAddresses(
        WalletAddress.Unified.new(unified),
        WalletAddress.ShieldedSapling.new(shieldedSapling),
        WalletAddress.Transparent.new(transparent),
        viewingKey
    )
}
