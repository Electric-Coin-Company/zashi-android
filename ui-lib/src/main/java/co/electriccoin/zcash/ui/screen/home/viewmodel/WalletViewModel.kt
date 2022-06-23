package co.electriccoin.zcash.ui.screen.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.db.entity.PendingTransaction
import cash.z.ecc.android.sdk.db.entity.Transaction
import cash.z.ecc.android.sdk.db.entity.isMined
import cash.z.ecc.android.sdk.db.entity.isSubmitSuccess
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.android.sdk.type.WalletBalance
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceKeys
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceSingleton
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import co.electriccoin.zcash.work.WorkIds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val walletCoordinator = co.electriccoin.zcash.global.WalletCoordinator.getInstance(application)

    /*
     * Using the Mutex may be overkill, but it ensures that if multiple calls are accidentally made
     * that they have a consistent ordering.
     */
    private val persistWalletMutex = Mutex()

    /**
     * Synchronizer that is retained long enough to survive configuration changes.
     */
    val synchronizer = walletCoordinator.synchronizer.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        null
    )

    /**
     * A flow of whether a backup of the user's wallet has been performed.
     */
    private val isBackupComplete = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
        emitAll(StandardPreferenceKeys.IS_USER_BACKUP_COMPLETE.observe(preferenceProvider))
    }

    val secretState: StateFlow<SecretState> = walletCoordinator.persistableWallet
        .combine(isBackupComplete) { persistableWallet: PersistableWallet?, isBackupComplete: Boolean ->
            if (null == persistableWallet) {
                SecretState.None
            } else if (!isBackupComplete) {
                SecretState.NeedsBackup(persistableWallet)
            } else {
                SecretState.Ready(persistableWallet)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            SecretState.Loading
        )

    // This needs to be refactored once we support pin lock
    val spendingKey = secretState
        .filterIsInstance<SecretState.Ready>()
        .map { it.persistableWallet }
        .map {
            val bip39Seed = withContext(Dispatchers.IO) {
                Mnemonics.MnemonicCode(it.seedPhrase.joinToString()).toSeed()
            }

            DerivationTool.deriveSpendingKeys(bip39Seed, it.network)[0]
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val walletSnapshot: StateFlow<WalletSnapshot?> = synchronizer
        .flatMapLatest {
            if (null == it) {
                flowOf(null)
            } else {
                it.toWalletSnapshot()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    // This is not the right API, because the transaction list could be very long and might need UI filtering
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val transactionSnapshot: StateFlow<List<Transaction>> = synchronizer
        .flatMapLatest {
            if (null == it) {
                flowOf(emptyList())
            } else {
                it.toTransactions()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            emptyList()
        )

    @OptIn(FlowPreview::class)
    val addresses: StateFlow<WalletAddresses?> = secretState
        .filterIsInstance<SecretState.Ready>()
        .map { WalletAddresses.new(it.persistableWallet) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    /**
     * Creates a wallet asynchronously and then persists it.  Clients observe
     * [secretState] to see the side effects.  This would be used for a user creating a new wallet.
     */
    /*
     * Although waiting for the wallet to be written and then read back is slower, it is probably
     * safer because it 1. guarantees the wallet is written to disk and 2. has a single source of truth.
     */
    fun persistNewWallet() {
        val application = getApplication<Application>()

        viewModelScope.launch {
            val newWallet = PersistableWallet.new(application)
            persistExistingWallet(newWallet)
        }
    }

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState]
     * to see the side effects.  This would be used for a user restoring a wallet from a backup.
     */
    fun persistExistingWallet(persistableWallet: PersistableWallet) {
        val application = getApplication<Application>()

        viewModelScope.launch {
            val preferenceProvider = EncryptedPreferenceSingleton.getInstance(application)
            persistWalletMutex.withLock {
                EncryptedPreferenceKeys.PERSISTABLE_WALLET.putValue(preferenceProvider, persistableWallet)
            }

            WorkIds.enableBackgroundSynchronization(application)
        }
    }

    /**
     * Asynchronously notes that the user has completed the backup steps, which means the wallet
     * is ready to use.  Clients observe [secretState] to see the side effects.  This would be used
     * for a user creating a new wallet.
     */
    fun persistBackupComplete() {
        val application = getApplication<Application>()

        viewModelScope.launch {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)

            // Use the Mutex here to avoid timing issues.  During wallet restore, persistBackupComplete()
            // is called prior to persistExistingWallet().  Although persistBackupComplete() should
            // complete quickly, it isn't guaranteed to complete before persistExistingWallet()
            // unless a mutex is used here.
            persistWalletMutex.withLock {
                StandardPreferenceKeys.IS_USER_BACKUP_COMPLETE.putValue(preferenceProvider, true)
            }
        }
    }

    /**
     * This method only has an effect if the synchronizer currently is loaded.
     */
    fun rescanBlockchain() {
        viewModelScope.launch {
            walletCoordinator.rescanBlockchain()
        }
    }

    /**
     * This asynchronously resets the SDK state.  This is non-destructive, as SDK state can be rederived.
     *
     * This could be used as a troubleshooting step in debugging.
     */
    fun resetSdk() {
        walletCoordinator.resetSdk()
    }

    /**
     * This asynchronously wipes the entire wallet state.
     *
     * This is destructive, as the seed phrase is deleted along with the SDK state.
     *
     * This could be used as part of testing, to quickly reset the app state.
     *
     * A more complete reset of app state can be performed in Android Settings, as this will not
     * clear application state beyond the SDK and wallet secret.
     */
    fun wipeEntireWallet() {
        walletCoordinator.wipeEntireWallet()
    }
}

/**
 * Represents the state of the wallet secret.
 */
sealed class SecretState {
    object Loading : SecretState()
    object None : SecretState()
    class NeedsBackup(val persistableWallet: PersistableWallet) : SecretState()
    class Ready(val persistableWallet: PersistableWallet) : SecretState()
}

// No good way around needing magic numbers for the indices
@Suppress("MagicNumber")
private fun Synchronizer.toWalletSnapshot() =
    combine(
        status, // 0
        processorInfo, // 1
        orchardBalances, // 2
        saplingBalances, // 3
        transparentBalances, // 4
        pendingTransactions.distinctUntilChanged() // 5
    ) { flows ->
        val pendingCount = (flows[5] as List<*>)
            .filterIsInstance(PendingTransaction::class.java)
            .count {
                it.isSubmitSuccess() && !it.isMined()
            }
        val orchardBalance = flows[2] as WalletBalance?
        val saplingBalance = flows[3] as WalletBalance?
        val transparentBalance = flows[4] as WalletBalance?

        WalletSnapshot(
            flows[0] as Synchronizer.Status,
            flows[1] as CompactBlockProcessor.ProcessorInfo,
            orchardBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0)),
            saplingBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0)),
            transparentBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0)),
            pendingCount
        )
    }

private fun Synchronizer.toTransactions() =
    combine(
        clearedTransactions.distinctUntilChanged(),
        pendingTransactions.distinctUntilChanged(),
        sentTransactions.distinctUntilChanged(),
        receivedTransactions.distinctUntilChanged()
    ) { cleared, pending, sent, received ->
        // TODO [#157]: Sort the transactions to show the most recent
        buildList<Transaction> {
            addAll(cleared)
            addAll(pending)
            addAll(sent)
            addAll(received)
        }
    }
