package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.WalletAddresses
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.sdk.extension.defaultForNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.global.getInstance
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.extension.throttle
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.totalBalance
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceKeys
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceSingleton
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt
import co.electriccoin.zcash.ui.screen.account.ext.getSortHeight
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val walletCoordinator = WalletCoordinator.getInstance(application)

    /*
     * Using the Mutex may be overkill, but it ensures that if multiple calls are accidentally made
     * that they have a consistent ordering.
     */
    private val persistWalletMutex = Mutex()

    /**
     * Synchronizer that is retained long enough to survive configuration changes.
     */
    val synchronizer =
        walletCoordinator.synchronizer.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    /**
     * A flow of the user's preferred fiat currency.
     */
    val preferredFiatCurrency: StateFlow<FiatCurrency?> =
        flow<FiatCurrency?> {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
            emitAll(StandardPreferenceKeys.PREFERRED_FIAT_CURRENCY.observe(preferenceProvider))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    /**
     * A flow of the wallet block synchronization state.
     */
    val walletRestoringState: StateFlow<WalletRestoringState> =
        flow {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
            emitAll(
                StandardPreferenceKeys.WALLET_RESTORING_STATE.observe(preferenceProvider).map { persistedNumber ->
                    WalletRestoringState.fromNumber(persistedNumber)
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            WalletRestoringState.NONE
        )

    /**
     * A flow of the wallet onboarding state.
     */
    private val onboardingState =
        flow {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
            emitAll(
                StandardPreferenceKeys.ONBOARDING_STATE.observe(preferenceProvider).map { persistedNumber ->
                    OnboardingState.fromNumber(persistedNumber)
                }
            )
        }

    val secretState: StateFlow<SecretState> =
        combine(
            walletCoordinator.persistableWallet,
            onboardingState
        ) { persistableWallet: PersistableWallet?, onboardingState: OnboardingState ->
            when {
                onboardingState == OnboardingState.NONE -> SecretState.None
                onboardingState == OnboardingState.NEEDS_WARN -> SecretState.NeedsWarning
                onboardingState == OnboardingState.NEEDS_BACKUP && persistableWallet != null -> {
                    SecretState.NeedsBackup(persistableWallet)
                }
                onboardingState == OnboardingState.READY && persistableWallet != null -> {
                    SecretState.Ready(persistableWallet)
                }
                else -> SecretState.None
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            SecretState.Loading
        )

    // This needs to be refactored once we support pin lock
    val spendingKey =
        secretState
            .filterIsInstance<SecretState.Ready>()
            .map { it.persistableWallet }
            .map {
                val bip39Seed =
                    withContext(Dispatchers.IO) {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val walletSnapshot: StateFlow<WalletSnapshot?> =
        synchronizer
            .flatMapLatest {
                if (null == it) {
                    flowOf(null)
                } else {
                    it.toWalletSnapshot()
                }
            }
            .throttle(1.seconds)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )

    val addresses: StateFlow<WalletAddresses?> =
        synchronizer
            .filterNotNull()
            .map {
                WalletAddresses.new(it)
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionHistoryState =
        synchronizer
            .filterNotNull()
            .flatMapLatest { synchronizer ->
                combine(
                    synchronizer.transactions,
                    synchronizer.status,
                    synchronizer.networkHeight.filterNotNull()
                ) {
                        transactions: List<TransactionOverview>,
                        status: Synchronizer.Status,
                        networkHeight: BlockHeight ->
                    val enhancedTransactions =
                        transactions
                            .sortedByDescending {
                                it.getSortHeight(networkHeight)
                            }
                            .map {
                                if (it.isSentTransaction) {
                                    TransactionOverviewExt(it, synchronizer.getRecipients(it).firstOrNull())
                                } else {
                                    TransactionOverviewExt(it, null)
                                }
                            }
                    if (status.isSyncing()) {
                        TransactionHistorySyncState.Syncing(enhancedTransactions.toPersistentList())
                    } else {
                        TransactionHistorySyncState.Done(enhancedTransactions.toPersistentList())
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = TransactionHistorySyncState.Loading
            )

    /**
     * A flow of the wallet balances state used for the UI layer. It combines [WalletSnapshot] with
     * [WalletRestoringState] and provides the correct [BalanceState] UI state.
     */
    val balanceState: StateFlow<BalanceState> =
        walletSnapshot
            .filterNotNull()
            .combine(walletRestoringState) {
                    walletSnapshot: WalletSnapshot, walletRestoringState: WalletRestoringState ->
                when (walletRestoringState) {
                    WalletRestoringState.NONE -> BalanceState.None
                    WalletRestoringState.INITIATING ->
                        BalanceState.Available(
                            totalBalance = walletSnapshot.totalBalance(),
                            spendableBalance = walletSnapshot.spendableBalance()
                        )
                    WalletRestoringState.RESTORING, WalletRestoringState.SYNCING -> {
                        if (walletSnapshot.spendableBalance().value == 0L &&
                            walletSnapshot.totalBalance().value > 0L
                        ) {
                            BalanceState.Loading(
                                totalBalance = walletSnapshot.totalBalance()
                            )
                        } else {
                            BalanceState.Available(
                                totalBalance = walletSnapshot.totalBalance(),
                                spendableBalance = walletSnapshot.spendableBalance()
                            )
                        }
                    }
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                BalanceState.None
            )

    /**
     * Creates a wallet asynchronously and then persists it.  Clients observe
     * [secretState] to see the side effects.  This would be used for a user creating a new wallet.
     */
    fun persistNewWallet() {
        /*
         * Although waiting for the wallet to be written and then read back is slower, it is probably
         * safer because it 1. guarantees the wallet is written to disk and 2. has a single source of truth.
         */

        val application = getApplication<Application>()

        viewModelScope.launch {
            val zcashNetwork = ZcashNetwork.fromResources(application)
            val newWallet =
                PersistableWallet.new(
                    application = application,
                    zcashNetwork = zcashNetwork,
                    endpoint = LightWalletEndpoint.defaultForNetwork(zcashNetwork),
                    walletInitMode = WalletInitMode.NewWallet
                )
            persistWallet(newWallet)
        }
    }

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState]
     * to see the side effects.  This would be used for a user restoring a wallet from a backup.
     */
    fun persistExistingWallet(persistableWallet: PersistableWallet) {
        persistWallet(persistableWallet)
    }

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState] to see the side effects.
     */
    private fun persistWallet(persistableWallet: PersistableWallet) {
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
    fun persistOnboardingState(onboardingState: OnboardingState) {
        val application = getApplication<Application>()

        viewModelScope.launch {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)

            // Use the Mutex here to avoid timing issues.  During wallet restore, persistOnboardingState()
            // is called prior to persistExistingWallet().  Although persistOnboardingState() should
            // complete quickly, it isn't guaranteed to complete before persistExistingWallet()
            // unless a mutex is used here.
            persistWalletMutex.withLock {
                StandardPreferenceKeys.ONBOARDING_STATE.putValue(preferenceProvider, onboardingState.toNumber())
            }
        }
    }

    /**
     * Asynchronously notes that the wallet has completed the initial wallet restoring block synchronization run.
     *
     * Note that in the current SDK implementation, we don't have any information about the block synchronization
     * state from the SDK, and thus, we need to note the wallet restoring state here on the client side.
     */
    fun persistWalletRestoringState(walletRestoringState: WalletRestoringState) {
        val application = getApplication<Application>()

        viewModelScope.launch {
            val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
            StandardPreferenceKeys.WALLET_RESTORING_STATE.putValue(preferenceProvider, walletRestoringState.toNumber())
        }
    }

    /**
     * This method only has an effect if the synchronizer currently is loaded.
     */
    fun rescanBlockchain() {
        viewModelScope.launch {
            walletCoordinator.rescanBlockchain()
            persistWalletRestoringState(WalletRestoringState.RESTORING)
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
     * This safely and asynchronously stops [Synchronizer].
     */
    fun closeSynchronizer() {
        val synchronizer = synchronizer.value
        if (null != synchronizer) {
            viewModelScope.launch {
                (synchronizer as SdkSynchronizer).close()
            }
        }
    }
}

/**
 * Represents the state of the wallet secret.
 */
sealed class SecretState {
    object Loading : SecretState()

    object None : SecretState()

    object NeedsWarning : SecretState()

    class NeedsBackup(val persistableWallet: PersistableWallet) : SecretState()

    class Ready(val persistableWallet: PersistableWallet) : SecretState()
}

// TODO [#529]: Localize Synchronizer Errors
// TODO [#529]: https://github.com/Electric-Coin-Company/zashi-android/issues/529

/**
 * Represents all kind of Synchronizer errors
 */
sealed class SynchronizerError {
    abstract fun getCauseMessage(): String?

    class Critical(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message
    }

    class Processor(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message
    }

    class Submission(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message
    }

    class Setup(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message
    }

    class Chain(val x: BlockHeight, val y: BlockHeight) : SynchronizerError() {
        override fun getCauseMessage(): String = "$x, $y"
    }
}

private fun Synchronizer.toCommonError(): Flow<SynchronizerError?> =
    callbackFlow {
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
        // 0
        status,
        // 1
        processorInfo,
        // 2
        orchardBalances,
        // 3
        saplingBalances,
        // 4
        transparentBalance,
        // 5
        progress,
        // 6
        toCommonError()
    ) { flows ->
        val orchardBalance = flows[2] as WalletBalance?
        val saplingBalance = flows[3] as WalletBalance?
        val transparentBalance = flows[4] as Zatoshi?

        val progressPercentDecimal = flows[5] as PercentDecimal

        WalletSnapshot(
            flows[0] as Synchronizer.Status,
            flows[1] as CompactBlockProcessor.ProcessorInfo,
            orchardBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0), Zatoshi(0)),
            saplingBalance ?: WalletBalance(Zatoshi(0), Zatoshi(0), Zatoshi(0)),
            transparentBalance ?: Zatoshi(0),
            progressPercentDecimal,
            flows[6] as SynchronizerError?
        )
    }

fun Synchronizer.Status.isSyncing() = this == Synchronizer.Status.SYNCING

fun Synchronizer.Status.isSynced() = this == Synchronizer.Status.SYNCED
