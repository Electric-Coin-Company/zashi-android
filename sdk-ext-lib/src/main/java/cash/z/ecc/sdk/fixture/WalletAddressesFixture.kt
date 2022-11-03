package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.type.UnifiedFullViewingKey
import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.WalletAddresses

object WalletAddressesFixture {
    // These fixture values are derived from the secret defined in PersistableWalletFixture
    val VIEWING_KEY = UnifiedFullViewingKey("uview17fme6ux853km45g9ep07djpfzeydxxgm22xpmr7arzxyutlusalgpqlx7suga4ahzywfuwz4jclm00u7g8u65qvvdt45kttnfunvschssg3h3g06txs9ja32vx3xa8dej3unnatgzjvd0vumk37t8es3ludldrtse3q6226ws7eq4q0ywz78nudwpepgdn7jmxz8yvp7k6gxkeynkam0f8aqf9qpeaej55zhkw39x7epayhndul0j4xjttdxxlnwcd09nr8svyx8j0zng0w6scx3m5unpkaqxcm3hslhlfg4caz7r8d4xy9wm7klkg79w7j0uyzec5s3yje20eg946r6rmkf532nfydu26s8q9ua7mwxw2j2ag7hfcuu652gw6uta03vlm05zju3a9rwc4h367kqzfqrcz35pdwdk2a7yqnk850un3ujxcvve45ueajgvtr6dj4ufszgqwdy0aedgmkalx2p7qed2suarwkr35dl0c8dnqp3")

    suspend fun new(
        unified: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING,
        legacySapling: String = WalletAddressFixture.LEGACY_SAPLING_ADDRESS_STRING,
        legacyTransparent: String = WalletAddressFixture.LEGACY_TRANSPARENT_ADDRESS_STRING,
        viewingKey: UnifiedFullViewingKey = VIEWING_KEY
    ) = WalletAddresses(
        WalletAddress.Unified.new(unified),
        WalletAddress.LegacySapling.new(legacySapling),
        WalletAddress.LegacyTransparent.new(legacyTransparent),
        viewingKey
    )
}
