package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddresses

object WalletAddressesFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture

    const val UNIFIED = "Unified GitHub Issue #161"
    const val SHIELDED_ORCHARD = "Shielded Orchard GitHub Issue #161"
    @Suppress("MaxLineLength")
    const val SHIELDED_SAPLING = "ztestsapling1475xtm56czrzmleqzzlu4cxvjjfsy2p6rv78q07232cpsx5ee52k0mn5jyndq09mampkgvrxnwg"
    const val TRANSPARENT = "tmXuTnE11JojToagTqxXUn6KvdxDE3iLKbp"
    const val VIEWING_KEY = "03feaa290589a20f795f302ba03847b0a6c9c2b571d75d80bc4ebb02382d0549da"

    fun new(
        unified: String = UNIFIED,
        shieldedOrchard: String = SHIELDED_ORCHARD,
        shieldedSapling: String = SHIELDED_SAPLING,
        transparent: String = TRANSPARENT,
        viewingKey: String = VIEWING_KEY
    ) = WalletAddresses(unified, shieldedOrchard, shieldedSapling, transparent, viewingKey)
}
