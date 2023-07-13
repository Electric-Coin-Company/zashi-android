package co.electriccoin.zcash.ui.screen.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.WalletAddresses
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.global.getInstance
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.HAS_SEED_PHRASE
import co.electriccoin.zcash.ui.common.OldSecurePreference
import co.electriccoin.zcash.ui.common.SEED_PHRASE
import co.electriccoin.zcash.ui.common.throttle
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceKeys
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceSingleton
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.history.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import co.electriccoin.zcash.ui.screen.transactiondetails.model.TransactionDetailsUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/zcash/secant-android-wallet/issues/292
class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val walletCoordinator = WalletCoordinator.getInstance(application)

    /*
     * Using the Mutex may be overkill, but it ensures that if multiple calls are accidentally made
     * that they have a consistent ordering.
     */
    private val persistWalletMutex = Mutex()

    /**
     * This preference is used to get the data from old app version before migration to compose.
     * Don't use for new value storage
     */
    private val oldSecurePreference by lazy { OldSecurePreference(application) }

    /**
     * Flow to determine weather we need to migrate old app or not
     */
    private val isMigrationRequiredFromOldWallet = MutableStateFlow(false)

    /**
     * Here we check if user is trying to migrate from old app to compose version. In old app we store local data
     * differently so we check if seed words are available then migrate them to new wallet.
     * This method will update [isMigrationRequiredFromOldWallet] and that will impact the [secretState]. For doing
     * the operation on IO thread to avoid violation we defined as a method
     */
    fun checkForOldAppMigration() {
        viewModelScope.launch(Dispatchers.IO) {
            isMigrationRequiredFromOldWallet.update { oldSecurePreference.getBoolean(HAS_SEED_PHRASE) && oldSecurePreference.getString(SEED_PHRASE).isNotBlank() }
        }
    }

    /**
     * Synchronizer that is retained long enough to survive configuration changes.
     */
    val synchronizer = walletCoordinator.synchronizer.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        null
    )

    /**
     * A flow of the user's preferred fiat currency.
     */
    val preferredFiatCurrency: StateFlow<FiatCurrency?> = flow<FiatCurrency?> {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
        emitAll(StandardPreferenceKeys.PREFERRED_FIAT_CURRENCY.observe(preferenceProvider))
    }.stateIn(
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

    /**
     * This flow will be used to update that user is authenticated to enter in the app or not.
     * This will used when user has enabled authentication and to track that it is enabled or not  we are using
     * @see isAuthenticationRequire
     */
    private val isUserAuthenticated = MutableStateFlow(false)

    /**
     * A flow of whether authentication require to allow user to use the app.
     * If authentication is not enabled by user we will return false,
     * if user has enabled authentication then we check isUserAuthenticated
     * @see isUserAuthenticated
     */
    private val isAuthenticationRequire = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
        emit(StandardPreferenceKeys.LAST_ENTERED_PIN.getValue(preferenceProvider).isNotBlank())
    }.combine(isUserAuthenticated) { isAuthenticationEnabled, isUserAuthenticated ->
        if (isAuthenticationEnabled) {
            isUserAuthenticated.not()
        } else {
            false
        }
    }

    val secretState: StateFlow<SecretState> = run {
        combine(walletCoordinator.persistableWallet, isBackupComplete, isAuthenticationRequire, isMigrationRequiredFromOldWallet) { persistableWallet: PersistableWallet?, isBackupComplete: Boolean, isAuthenticationRequire: Boolean,
            isMigrationRequired ->
            if (isMigrationRequired) {
                SecretState.NeedMigrationFromOldApp
            } else if (isAuthenticationRequire) {
                SecretState.NeedAuthentication
            } else if (null == persistableWallet) {
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
    }

    // This needs to be refactored once we support pin lock
    val spendingKey = secretState
        .filterIsInstance<SecretState.Ready>()
        .map { it.persistableWallet }
        .map {
            val bip39Seed = withContext(Dispatchers.IO) {
                Mnemonics.MnemonicCode(it.seedPhrase.joinToString()).toSeed()
            }
            DerivationTool.getInstance().deriveUnifiedSpendingKey(
                seed = bip39Seed,
                network = it.network,
                account = Account.DEFAULT
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    val walletSnapshot: StateFlow<WalletSnapshot?> = synchronizer
        .flatMapLatest {
            it?.toWalletSnapshot() ?: flowOf(null)
        }
        .throttle(1.seconds)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    // This is not the right API, because the transaction list could be very long and might need UI filtering
    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionSnapshot: StateFlow<ImmutableList<TransactionOverview>> = synchronizer
        .flatMapLatest {
            it?.transactions?.map { list -> list.toPersistentList() } ?: flowOf(persistentListOf())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            persistentListOf()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun transactionUiModel(transactionId: Long, synchronizer: Synchronizer): StateFlow<TransactionDetailsUIModel?> {
        return synchronizer.transactions
            .distinctUntilChanged()
            .flatMapLatest { transactionSnapshotList ->
                val transactionOverview = transactionSnapshotList.find { it.id == transactionId }
                    ?: return@flatMapLatest emptyFlow()
                combine(
                    if (transactionOverview.isSentTransaction) synchronizer.getRecipients(transactionOverview) else flowOf(TransactionRecipient.Account(Account.DEFAULT)),
                    synchronizer.getMemos(transactionOverview),
                    synchronizer.networkHeight
                ) { transactionRecipient, memo, networkHeight ->
                    TransactionDetailsUIModel(transactionOverview, transactionRecipient, synchronizer.network, networkHeight, memo)
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )
    }

    val addresses: StateFlow<WalletAddresses?> = synchronizer
        .filterNotNull()
        .map {
            WalletAddresses.new(it)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionHistoryState = synchronizer
        .filterNotNull()
        .flatMapLatest {
            it.transactions
                .combine(it.status) { transactions: List<TransactionOverview>, status: Synchronizer.Status ->
                    if (status.isSyncing()) {
                        TransactionHistorySyncState.Syncing(transactions.toPersistentList())
                    } else {
                        TransactionHistorySyncState.Done(transactions.toPersistentList())
                    }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = TransactionHistorySyncState.Loading
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
            val newWallet = PersistableWallet.new(application, ZcashNetwork.fromResources(application))
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
     * This asynchronously update the SecretState.
     * If user is authenticated then only we allow user to enter in the app
     */
    fun updateAuthenticationState(isUserAuthenticated: Boolean) {
        this.isUserAuthenticated.update { isUserAuthenticated }
    }
}

/**
 * Represents the state of the wallet secret.
 */
sealed class SecretState {
    object Loading : SecretState()
    object NeedMigrationFromOldApp: SecretState()
    object NeedAuthentication : SecretState()
    object None : SecretState()
    class NeedsBackup(val persistableWallet: PersistableWallet) : SecretState()
    class Ready(val persistableWallet: PersistableWallet) : SecretState()
}

/**
 * Represents all kind of Synchronizer errors
 */
// TODO [#529]: Localize Synchronizer Errors
// TODO [#529]: https://github.com/zcash/secant-android-wallet/issues/529
sealed class SynchronizerError {
    abstract fun getCauseMessage(): String?

    class Critical(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.localizedMessage
    }

    class Processor(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.localizedMessage
    }

    class Submission(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.localizedMessage
    }

    class Setup(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.localizedMessage
    }

    class Chain(val x: BlockHeight, val y: BlockHeight) : SynchronizerError() {
        override fun getCauseMessage(): String = "$x, $y"
    }
}

private fun Synchronizer.toCommonError(): Flow<SynchronizerError?> = callbackFlow {
    // just for initial default value emit
    trySend(null)

    onCriticalErrorHandler = {
        Twig.error { "WALLET - Error Critical: $it" }
        trySend(SynchronizerError.Critical(it))
        false
    }
    onProcessorErrorHandler = {
        Twig.error { "WALLET - Error Processor: $it" }
        trySend(SynchronizerError.Processor(it))
        false
    }
    onSubmissionErrorHandler = {
        Twig.error { "WALLET - Error Submission: $it" }
        trySend(SynchronizerError.Submission(it))
        false
    }
    onSetupErrorHandler = {
        Twig.error { "WALLET - Error Setup: $it" }
        trySend(SynchronizerError.Setup(it))
        false
    }
    onChainErrorHandler = { x, y ->
        Twig.error { "WALLET - Error Chain: $x, $y" }
        trySend(SynchronizerError.Chain(x, y))
    }

    awaitClose {
        // nothing to close here
    }
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
        progress, // 5
        toCommonError() // 6
    ) { flows ->
        val orchardBalance = flows[2] as WalletBalance?
        val saplingBalance = flows[3] as WalletBalance?
        val transparentBalance = flows[4] as WalletBalance?

        val progressPercentDecimal = flows[5] as PercentDecimal

        WalletSnapshot(
            flows[0] as Synchronizer.Status,
            flows[1] as CompactBlockProcessor.ProcessorInfo,
            orchardBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0)),
            saplingBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0)),
            transparentBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0)),
            progressPercentDecimal,
            flows[6] as SynchronizerError?
        )
    }

private fun Synchronizer.Status.isSyncing() = this == Synchronizer.Status.SYNCING
