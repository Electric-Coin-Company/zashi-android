package cash.z.ecc.sdk.model

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.tool.DerivationTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class WalletAddresses(
    val unified: String,
    val shieldedOrchard: String,
    val shieldedSapling: String,
    val transparent: String,
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

            // Dispatchers needed until an SDK is published with the implementation of
            // https://github.com/zcash/zcash-android-wallet-sdk/issues/269
            val viewingKey = withContext(Dispatchers.IO) {
                DerivationTool.deriveUnifiedViewingKeys(bip39Seed, persistableWallet.network)[0]
            }.extpub

            val shieldedSaplingAddress = withContext(Dispatchers.IO) {
                DerivationTool.deriveShieldedAddress(bip39Seed, persistableWallet.network)
            }

            val transparentAddress = withContext(Dispatchers.IO) {
                DerivationTool.deriveTransparentAddress(bip39Seed, persistableWallet.network)
            }

            // TODO [#161]: Pending SDK support, fix providing correct values for the unified
            return WalletAddresses(
                unified = "Unified GitHub Issue #161",
                shieldedOrchard = "Shielded Orchard GitHub Issue #161",
                shieldedSapling = shieldedSaplingAddress,
                transparent = transparentAddress,
                viewingKey = viewingKey
            )
        }
    }
}
