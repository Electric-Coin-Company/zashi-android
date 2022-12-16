package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Account

data class WalletAddresses(
    val unified: WalletAddress.Unified,
    val sapling: WalletAddress.Sapling,
    val transparent: WalletAddress.Transparent
) {
    // Override to prevent leaking details in logs
    override fun toString() = "WalletAddresses"

    companion object {
        suspend fun new(synchronizer: Synchronizer): WalletAddresses {
            val unifiedAddress = WalletAddress.Unified.new(
                synchronizer.getUnifiedAddress(Account.DEFAULT)
            )
            val saplingAddress = WalletAddress.Sapling.new(
                synchronizer.getSaplingAddress(Account.DEFAULT)
            )
            val transparentAddress = WalletAddress.Transparent.new(
                synchronizer.getTransparentAddress(Account.DEFAULT)
            )

            // TODO [#161]: Pending SDK support, fix providing correct values for the unified
            // TODO [#161]: https://github.com/zcash/secant-android-wallet/issues/161
            return WalletAddresses(
                unified = unifiedAddress,
                sapling = saplingAddress,
                transparent = transparentAddress
            )
        }
    }
}
