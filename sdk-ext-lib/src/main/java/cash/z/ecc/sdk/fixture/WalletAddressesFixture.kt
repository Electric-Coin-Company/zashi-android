package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.WalletAddresses

object WalletAddressesFixture {

    suspend fun new(
        unified: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING,
        legacySapling: String = WalletAddressFixture.LEGACY_SAPLING_ADDRESS_STRING,
        legacyTransparent: String = WalletAddressFixture.LEGACY_TRANSPARENT_ADDRESS_STRING
    ) = WalletAddresses(
        WalletAddress.Unified.new(unified),
        WalletAddress.LegacySapling.new(legacySapling),
        WalletAddress.LegacyTransparent.new(legacyTransparent)
    )
}
