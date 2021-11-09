package cash.z.ecc.sdk

import android.content.Context
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.android.sdk.type.UnifiedViewingKey
import cash.z.ecc.sdk.model.PersistableWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Synchronizer needs a Companion object
// https://github.com/zcash/zcash-android-wallet-sdk/issues/310
object SynchronizerCompanion {
    suspend fun load(context: Context, persistableWallet: PersistableWallet): Synchronizer {
        val config = persistableWallet.toConfig()
        val initializer = withContext(Dispatchers.IO) { Initializer(context, config) }
        return withContext(Dispatchers.IO) { Synchronizer(initializer) }
    }
}

private suspend fun PersistableWallet.deriveViewingKey(): UnifiedViewingKey {
    // Dispatcher needed because SecureRandom is loaded, which is slow and performs IO
    // https://github.com/zcash/kotlin-bip39/issues/13
    val bip39Seed = withContext(Dispatchers.IO) {
        Mnemonics.MnemonicCode(seedPhrase.phrase).toSeed()
    }

    // Dispatchers needed until an SDK is published with the implementation of
    // https://github.com/zcash/zcash-android-wallet-sdk/issues/269
    val viewingKey = withContext(Dispatchers.IO) {
        DerivationTool.deriveUnifiedViewingKeys(bip39Seed, network)[0]
    }

    return viewingKey
}

private suspend fun PersistableWallet.toConfig(): Initializer.Config {
    val network = network
    val vk = deriveViewingKey()

    return Initializer.Config {
        it.importWallet(vk, birthday.height, network, network.defaultHost, network.defaultPort)
    }
}
