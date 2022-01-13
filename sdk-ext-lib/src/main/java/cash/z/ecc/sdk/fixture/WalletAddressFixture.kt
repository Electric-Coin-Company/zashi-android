package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress

object WalletAddressFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture

    // TODO [#161]: Pending SDK support
    const val UNIFIED_ADDRESS_STRING = "Unified GitHub Issue #161"

    @Suppress("MaxLineLength")
    const val SHIELDED_SAPLING_ADDRESS_STRING = "ztestsapling1475xtm56czrzmleqzzlu4cxvjjfsy2p6rv78q07232cpsx5ee52k0mn5jyndq09mampkgvrxnwg"
    const val TRANSPARENT_ADDRESS_STRING = "tmXuTnE11JojToagTqxXUn6KvdxDE3iLKbp"

    suspend fun unified() = WalletAddress.Unified.new(UNIFIED_ADDRESS_STRING)
    suspend fun shieldedSapling() = WalletAddress.ShieldedSapling.new(SHIELDED_SAPLING_ADDRESS_STRING)
    suspend fun transparent() = WalletAddress.Transparent.new(TRANSPARENT_ADDRESS_STRING)
}
