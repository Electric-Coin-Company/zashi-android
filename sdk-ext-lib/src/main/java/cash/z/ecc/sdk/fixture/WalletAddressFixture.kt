package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress

object WalletAddressFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture

    // TODO [#161]: Pending SDK support
    // TODO [#161]: https://github.com/zcash/secant-android-wallet/issues/161
    const val UNIFIED_ADDRESS_STRING = "Unified GitHub Issue #161"

    @Suppress("MaxLineLength")
    const val LEGACY_SAPLING_ADDRESS_STRING = "ztestsapling1475xtm56czrzmleqzzlu4cxvjjfsy2p6rv78q07232cpsx5ee52k0mn5jyndq09mampkgvrxnwg"
    const val LEGACY_TRANSPARENT_ADDRESS_STRING = "tmXuTnE11JojToagTqxXUn6KvdxDE3iLKbp"

    suspend fun unified() = WalletAddress.Unified.new(UNIFIED_ADDRESS_STRING)
    suspend fun legacySapling() = WalletAddress.LegacySapling.new(LEGACY_SAPLING_ADDRESS_STRING)
    suspend fun legacyTransparent() = WalletAddress.LegacyTransparent.new(LEGACY_TRANSPARENT_ADDRESS_STRING)
}
