package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress

object WalletAddressFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture

    // TODO [#161]: Pending SDK support
    // TODO [#161]: https://github.com/zcash/secant-android-wallet/issues/161
    const val UNIFIED_ADDRESS_STRING = "Unified GitHub Issue #161"

    @Suppress("MaxLineLength")
    const val SAPLING_ADDRESS_STRING = "zs1hf72k87gev2qnvg9228vn2xt97adfelju2hm2ap4xwrxkau5dz56mvkeseer3u8283wmy7skt4u"
    const val TRANSPARENT_ADDRESS_STRING = "t1QZMTZaU1EwXppCLL5dR6U9y2M4ph3CSPK"

    suspend fun unified() = WalletAddress.Unified.new(UNIFIED_ADDRESS_STRING)
    suspend fun sapling() = WalletAddress.Sapling.new(SAPLING_ADDRESS_STRING)
    suspend fun transparent() = WalletAddress.Transparent.new(TRANSPARENT_ADDRESS_STRING)
}
