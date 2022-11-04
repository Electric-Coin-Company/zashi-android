package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Account

data class WalletAddresses(
    val unified: WalletAddress.Unified,
    val legacySapling: WalletAddress.LegacySapling,
    val legacyTransparent: WalletAddress.LegacyTransparent
) {
    // Override to prevent leaking details in logs
    override fun toString() = "WalletAddresses"

    companion object {
        suspend fun new(synchronizer: Synchronizer): WalletAddresses {
            val legacySaplingAddress = WalletAddress.LegacySapling.new(
                synchronizer.getLegacySaplingAddress(Account.DEFAULT)
            )

            val legacyTransparentAddress = WalletAddress.LegacyTransparent.new(
                synchronizer.getLegacyTransparentAddress(Account.DEFAULT)
            )

            // TODO [#161]: Pending SDK support, fix providing correct values for the unified
            // TODO [#161]: https://github.com/zcash/secant-android-wallet/issues/161
            return WalletAddresses(
                unified = WalletAddress.Unified.new("Unified GitHub Issue #161"),
                legacySapling = legacySaplingAddress,
                legacyTransparent = legacyTransparentAddress
            )
        }
    }
}
