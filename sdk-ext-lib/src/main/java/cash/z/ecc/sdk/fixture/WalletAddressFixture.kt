package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress

@Suppress("MaxLineLength")
object WalletAddressFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture
    const val UNIFIED_ADDRESS_STRING = "utest1qwerty6tj6989fcf0r57aavfnypj0krtxnmz93ty7ujr7d0spdxnz97kfdcugq9ndglvyg7v89m78dparu33q0putwaf2kvnsypsh8juzmcdvnedjlrrwhel9ldr203p7wc922luxup"

    const val SAPLING_ADDRESS_STRING = "zs1hf72k87gev2qnvg9228vn2xt97adfelju2hm2ap4xwrxkau5dz56mvkeseer3u8283wmy7skt4u"
    const val TRANSPARENT_ADDRESS_STRING = "t1QZMTZaU1EwXppCLL5dR6U9y2M4ph3CSPK"

    suspend fun unified() = WalletAddress.Unified.new(UNIFIED_ADDRESS_STRING)
    suspend fun sapling() = WalletAddress.Sapling.new(SAPLING_ADDRESS_STRING)
    suspend fun transparent() = WalletAddress.Transparent.new(TRANSPARENT_ADDRESS_STRING)
}
