package cash.z.ecc.sdk.model

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.tool.DerivationTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class WalletAddresses(
    val unified: WalletAddress.Unified,
    val shieldedSapling: WalletAddress.ShieldedSapling,
    val transparent: WalletAddress.Transparent,
    val viewingKey: String
) {
    // Override to prevent leaking details in logs
    override fun toString() = "WalletAddresses"

    companion object {
        suspend fun new(persistableWallet: PersistableWallet): WalletAddresses {
            // Dispatcher needed because SecureRandom is loaded, which is slow and performs IO
            // https://github.com/zcash/kotlin-bip39/issues/13
            val bip39Seed = withContext(Dispatchers.IO) {
                Mnemonics.MnemonicCode(persistableWallet.seedPhrase.joinToString()).toSeed()
            }

            val viewingKey = DerivationTool.deriveUnifiedViewingKeys(bip39Seed, persistableWallet.network)[0].extpub

            val shieldedSaplingAddress = DerivationTool.deriveShieldedAddress(
                bip39Seed,
                persistableWallet.network
            ).let {
                WalletAddress.ShieldedSapling.new(it)
            }

            val transparentAddress = DerivationTool.deriveTransparentAddress(
                bip39Seed,
                persistableWallet.network
            ).let {
                WalletAddress.Transparent.new(it)
            }

            // TODO [#161]: Pending SDK support, fix providing correct values for the unified
            return WalletAddresses(
                unified = WalletAddress.Unified.new("Unified GitHub Issue #161"),
                shieldedSapling = shieldedSaplingAddress,
                transparent = transparentAddress,
                viewingKey = viewingKey
            )
        }
    }
}
