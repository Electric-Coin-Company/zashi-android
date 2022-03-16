package co.electriccoin.zcash.global

import android.content.Context
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.android.sdk.type.UnifiedViewingKey
import cash.z.ecc.android.sdk.type.ZcashNetwork
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.spackle.LazyWithArgument
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceKeys
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class WalletCoordinator(context: Context) {
    companion object {
        private val lazy = LazyWithArgument<Context, WalletCoordinator> { WalletCoordinator(it) }

        fun getInstance(context: Context) = lazy.getInstance(context)
    }

    private val applicationContext = context.applicationContext

    /*
     * We want a global scope that is independent of the lifecycles of either
     * WorkManager or the UI.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private val walletScope = CoroutineScope(GlobalScope.coroutineContext + Dispatchers.Main)

    private val synchronizerMutex = Mutex()

    /**
     * A flow of the user's stored wallet.  Null indicates that no wallet has been stored.
     */
    val persistableWallet = flow {
        // EncryptedPreferenceSingleton.getInstance() is a suspending function, which is why we need
        // the flow builder to provide a coroutine context.
        val encryptedPreferenceProvider = EncryptedPreferenceSingleton.getInstance(applicationContext)

        emitAll(EncryptedPreferenceKeys.PERSISTABLE_WALLET.observe(encryptedPreferenceProvider))
    }

    /**
     * Synchronizer for the Zcash SDK. Emits null until a wallet secret is persisted.
     *
     * Note that this synchronizer is closed as soon as it stops being collected.  For UI use
     * cases, see [WalletViewModel].
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val synchronizer: StateFlow<Synchronizer?> = persistableWallet
        .filterNotNull()
        .flatMapConcat {
            callbackFlow {
                val initializer = Initializer.new(context, it.toConfig())
                val synchronizer = synchronizerMutex.withLock {
                    val synchronizer = Synchronizer.new(initializer)
                    synchronizer.start(walletScope)
                }

                trySend(synchronizer)
                awaitClose {
                    synchronizer.stop()
                }
            }
        }.stateIn(
            walletScope, SharingStarted.WhileSubscribed(),
            null
        )

    /**
     * Rescans the blockchain.
     *
     * In order for a rescan to occur, the synchronizer must be loaded already
     * which would happen if the UI is collecting it.
     *
     * @return True if the rewind was performed and false if the wipe was not performed.
     */
    suspend fun rescanBlockchain(): Boolean {
        synchronizerMutex.withLock {
            synchronizer.value?.let {
                it.rewindToNearestHeight(it.latestBirthdayHeight, true)
                return true
            }
        }

        return false
    }

    /**
     * Wipes the wallet.
     *
     * In order for a wipe to occur, the synchronizer must be loaded already
     * which would happen if the UI is collecting it.
     *
     * @return True if the wipe was performed and false if the wipe was not performed.
     */
    suspend fun wipeWallet(): Boolean {
        /*
         * This implementation could perhaps be a little brittle due to needing to stop and start the
         * synchronizer.  If another client is interacting with the synchronizer at the same time,
         * it isn't well defined exactly what the behavior should be.
         *
         * Possible enhancements to improve this:
         *  - Hide the synchronizer from clients; prefer to add additional APIs to WalletViewModel
         *    which delegate to the synchronizer
         *  - Add a private StateFlow to WalletCoordinator to signal internal operations which should
         *    cancel the synchronizer for other observers. Modify synchronizer flow to use a combine
         *    operator to check the private stateflow.  When initiating a wipe, set that private
         *    StateFlow to cancel other observers of the synchronizer.
         */

        synchronizerMutex.withLock {
            synchronizer.value?.let {
                // There is a minor race condition here.  With the right timing, it is possible
                // that the collection of the Synchronizer flow is canceled during an erase.
                // In such a situation, the Synchronizer would be restarted at the end of
                // this method even though it shouldn't.  By checking for referential equality at
                // the end, we can reduce that timing gap.
                val wasStarted = it.isStarted
                if (wasStarted) {
                    it.stop()
                }

                Initializer.erase(
                    applicationContext,
                    ZcashNetwork.fromResources(applicationContext)
                )

                if (wasStarted && synchronizer.value === it) {
                    it.start(walletScope)
                }

                return true
            }
        }

        return false
    }
}

private suspend fun PersistableWallet.deriveViewingKey(): UnifiedViewingKey {
    // Dispatcher needed because SecureRandom is loaded, which is slow and performs IO
    // https://github.com/zcash/kotlin-bip39/issues/13
    val bip39Seed = withContext(Dispatchers.IO) {
        Mnemonics.MnemonicCode(seedPhrase.joinToString()).toSeed()
    }

    return DerivationTool.deriveUnifiedViewingKeys(bip39Seed, network)[0]
}

private suspend fun PersistableWallet.toConfig(): Initializer.Config {
    val network = network
    val vk = deriveViewingKey()

    return Initializer.Config {
        it.importWallet(vk, birthday?.height, network, network.defaultHost, network.defaultPort)
    }
}
