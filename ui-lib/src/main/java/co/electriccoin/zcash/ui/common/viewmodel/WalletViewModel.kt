package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
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
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.WalletAddresses
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.tool.DerivationTool
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.preference.api.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.extension.throttle
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.hasChangePending
import co.electriccoin.zcash.ui.common.model.hasValuePending
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.totalBalance
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.usecase.AvailableServersProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
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
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
@Suppress("LongParameterList")
class WalletViewModel(
    application: Application,
    observeSynchronizer: ObserveSynchronizerUseCase,
    private val walletCoordinator: WalletCoordinator,
    private val walletRepository: WalletRepository,
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val getAvailableServers: AvailableServersProvider
) : AndroidViewModel(application) {
    /**
     * Synchronizer that is retained long enough to survive configuration changes.
     */
    val synchronizer = observeSynchronizer()

    /**
     * A flow of the wallet block synchronization state.
     */
    val walletRestoringState: StateFlow<WalletRestoringState> =
        flow {
            emitAll(
                StandardPreferenceKeys.WALLET_RESTORING_STATE
                    .observe(standardPreferenceProvider).map { persistedNumber ->
                        WalletRestoringState.fromNumber(persistedNumber)
                    }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            WalletRestoringState.NONE
        )

    /**
     * A flow of the wallet current state information that should be displayed in screens top app bar.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val walletStateInformation: StateFlow<TopAppBarSubTitleState> =
        synchronizer
            .filterNotNull()
            .flatMapLatest { synchronizer ->
                combine(
                    synchronizer.status,
                    walletRestoringState
                ) { status: Synchronizer.Status?, walletRestoringState: WalletRestoringState ->
                    if (Synchronizer.Status.DISCONNECTED == status) {
                        TopAppBarSubTitleState.Disconnected
                    } else if (WalletRestoringState.RESTORING == walletRestoringState) {
                        TopAppBarSubTitleState.Restoring
                    } else {
                        TopAppBarSubTitleState.None
                    }
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                TopAppBarSubTitleState.None
            )

    /**
     * A flow of the wallet onboarding state.
     */
    private val onboardingState =
        flow {
            emitAll(
                StandardPreferenceKeys.ONBOARDING_STATE.observe(standardPreferenceProvider).map { persistedNumber ->
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
                runCatching {
                    WalletAddresses.new(it)
                }.onFailure {
                    Twig.warn { "Wait until the SDK starts providing the addresses" }
                }.getOrNull()
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
                    synchronizer.networkHeight
                ) { transactions: List<TransactionOverview>,
                    status: Synchronizer.Status,
                    networkHeight: BlockHeight? ->
                    val enhancedTransactions =
                        transactions
                            .sortedByDescending {
                                it.getSortHeight(networkHeight)
                            }
                            .map {
                                if (it.isSentTransaction) {
                                    val recipient = synchronizer.getRecipients(it).firstOrNull()
                                    TransactionOverviewExt(
                                        overview = it,
                                        recipient = recipient,
                                        recipientAddressType =
                                            if (recipient != null && (recipient is TransactionRecipient.Address)) {
                                                synchronizer.validateAddress(recipient.addressValue)
                                            } else {
                                                null
                                            }
                                    )
                                } else {
                                    // Note that recipients can only be queried for sent transactions
                                    TransactionOverviewExt(
                                        overview = it,
                                        recipient = null,
                                        recipientAddressType = null
                                    )
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
     * A flow of the wallet balances state used for the UI layer. It's computed form [WalletSnapshot]'s properties
     * and provides the result [BalanceState] UI state.
     */
    val balanceState: StateFlow<BalanceState> =
        walletSnapshot
            .filterNotNull()
            .map { snapshot ->
                when {
                    // Show the loader only under these conditions:
                    // - Available balance is currently zero AND total balance is non-zero
                    // - And wallet has some ChangePending or ValuePending in progress
                    (
                        snapshot.spendableBalance().value == 0L &&
                            snapshot.totalBalance().value > 0L &&
                            (snapshot.hasChangePending() || snapshot.hasValuePending())
                    ) -> {
                        BalanceState.Loading(
                            totalBalance = snapshot.totalBalance()
                        )
                    }

                    else -> {
                        BalanceState.Available(
                            totalBalance = snapshot.totalBalance(),
                            spendableBalance = snapshot.spendableBalance()
                        )
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
                    endpoint = getAvailableServers().first(),
                    walletInitMode = WalletInitMode.NewWallet
                )
            walletRepository.persistWallet(newWallet)
        }
    }

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState]
     * to see the side effects.  This would be used for a user restoring a wallet from a backup.
     */
    fun persistExistingWallet(persistableWallet: PersistableWallet) {
        walletRepository.persistWallet(persistableWallet)
    }

    fun persistOnboardingState(onboardingState: OnboardingState) {
        walletRepository.persistOnboardingState(onboardingState)
    }

    /**
     * Asynchronously notes that the wallet has completed the initial wallet restoring block synchronization run.
     *
     * Note that in the current SDK implementation, we don't have any information about the block synchronization
     * state from the SDK, and thus, we need to note the wallet restoring state here on the client side.
     */
    fun persistWalletRestoringState(walletRestoringState: WalletRestoringState) {
        viewModelScope.launch {
            StandardPreferenceKeys.WALLET_RESTORING_STATE.putValue(
                standardPreferenceProvider,
                walletRestoringState.toNumber()
            )
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

    private fun clearAppStateFlow(): Flow<Boolean> =
        callbackFlow {
            viewModelScope.launch {
                val standardPrefsCleared =
                    standardPreferenceProvider
                        .clearPreferences()
                val encryptedPrefsCleared =
                    encryptedPreferenceProvider
                        .clearPreferences()

                Twig.info { "Both preferences cleared: ${standardPrefsCleared && encryptedPrefsCleared}" }

                trySend(standardPrefsCleared && encryptedPrefsCleared)
            }

            awaitClose {
                // Nothing to close here
            }
        }

    fun deleteWalletFlow(activity: Activity): Flow<Boolean> =
        callbackFlow {
            Twig.info { "Delete wallet: Requested" }

            val synchronizer = synchronizer.value
            if (null != synchronizer) {
                viewModelScope.launch {
                    (synchronizer as SdkSynchronizer).closeFlow().collect {
                        Twig.info { "Delete wallet: SDK closed" }

                        walletCoordinator.deleteSdkDataFlow().collect { isSdkErased ->
                            Twig.info { "Delete wallet: Erase SDK result: $isSdkErased" }
                            if (!isSdkErased) {
                                trySend(false)
                            }

                            clearAppStateFlow().collect { isAppErased ->
                                Twig.info { "Delete wallet: Erase App result: $isAppErased" }
                                if (!isAppErased) {
                                    trySend(false)
                                } else {
                                    trySend(true)
                                    activity.run {
                                        finish()
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            awaitClose {
                // Nothing to close
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
