package cash.z.ecc.sdk.model

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.android.sdk.type.UnifiedFullViewingKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class WalletAddresses(
    val unified: WalletAddress.Unified,
    val legacySapling: WalletAddress.LegacySapling,
    val transparent: WalletAddress.Transparent,
    val viewingKey: UnifiedFullViewingKey
) {
    // Override to prevent leaking details in logs
    override fun toString() = "WalletAddresses"

    companion object {
        suspend fun new(persistableWallet: PersistableWallet, synchronizer: Synchronizer): WalletAddresses {
            // Dispatcher needed because SecureRandom is loaded, which is slow and performs IO
            // https://github.com/zcash/kotlin-bip39/issues/13
            val bip39Seed = withContext(Dispatchers.IO) {
                Mnemonics.MnemonicCode(persistableWallet.seedPhrase.joinToString()).toSeed()
            }

            val viewingKey = DerivationTool.deriveUnifiedFullViewingKeys(bip39Seed, persistableWallet.network, 1)[0]

            val legacySaplingAddress = WalletAddress.LegacySapling.new(
                synchronizer.getLegacySaplingAddress(Account.DEFAULT)
            )

            val transparentAddress = DerivationTool.deriveTransparentAddress(
                seed = bip39Seed,
                network = persistableWallet.network,
                account = Account.DEFAULT,
                index = 0
            ).let {
                WalletAddress.Transparent.new(it)
            }

            // TODO [#161]: Pending SDK support, fix providing correct values for the unified
            // TODO [#161]: https://github.com/zcash/secant-android-wallet/issues/161
            return WalletAddresses(
                unified = WalletAddress.Unified.new("Unified GitHub Issue #161"),
                legacySapling = legacySaplingAddress,
                transparent = transparentAddress,
                viewingKey = viewingKey
            )
        }
    }
}
