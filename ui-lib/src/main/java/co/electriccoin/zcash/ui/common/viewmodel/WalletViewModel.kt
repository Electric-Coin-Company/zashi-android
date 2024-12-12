package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.repository.BalanceRepository
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveCurrentTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetAddressBookUseCase
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt
import co.electriccoin.zcash.ui.screen.account.ext.getSortHeight
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import com.flexa.core.Flexa
import com.flexa.identity.buildIdentity
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
@Suppress("LongParameterList", "TooManyFunctions")
class WalletViewModel(
    application: Application,
    balanceRepository: BalanceRepository,
    private val walletCoordinator: WalletCoordinator,
    private val walletRepository: WalletRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val getAvailableServers: GetDefaultServersProvider,
    private val resetAddressBook: ResetAddressBookUseCase,
    private val isFlexaAvailable: IsFlexaAvailableUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val observeCurrentTransactions: ObserveCurrentTransactionsUseCase
) : AndroidViewModel(application) {
    val synchronizer = walletRepository.synchronizer

    val walletRestoringState = walletRepository.walletRestoringState

    val walletStateInformation = walletRepository.walletStateInformation

    val secretState: StateFlow<SecretState> = walletRepository.secretState

    val currentWalletSnapshot: StateFlow<WalletSnapshot?> = walletRepository.currentWalletSnapshot

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionHistoryState =
        synchronizer
            .filterNotNull()
            .flatMapLatest { synchronizer ->
                combine(
                    observeCurrentTransactions(),
                    synchronizer.status,
                    synchronizer.networkHeight
                ) { transactions: List<TransactionOverview>?,
                    status: Synchronizer.Status,
                    networkHeight: BlockHeight? ->
                    val enhancedTransactions =
                        transactions
                            .orEmpty()
                            .sortedByDescending {
                                it.getSortHeight(networkHeight)
                            }
                            .map {
                                val outputs = synchronizer.getTransactionOutputs(it)

                                if (it.isSentTransaction) {
                                    val recipient = synchronizer.getRecipients(it).firstOrNull()
                                    TransactionOverviewExt(
                                        overview = it,
                                        recipient = recipient,
                                        recipientAddressType =
                                            if (recipient != null &&
                                                (recipient is TransactionRecipient.RecipientAddress)
                                            ) {
                                                synchronizer.validateAddress(recipient.addressValue)
                                            } else {
                                                null
                                            },
                                        transactionOutputs = outputs,
                                    )
                                } else {
                                    // Note that recipients can only be queried for sent transactions
                                    TransactionOverviewExt(
                                        overview = it,
                                        recipient = null,
                                        recipientAddressType = null,
                                        transactionOutputs = outputs,
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

    val isExchangeRateUsdOptedIn = exchangeRateRepository.isExchangeRateUsdOptedIn

    val exchangeRateUsd = exchangeRateRepository.state

    val balanceState = balanceRepository.state

    fun refreshExchangeRateUsd() {
        exchangeRateRepository.refreshExchangeRateUsd()
    }

    fun optInExchangeRateUsd(optIn: Boolean) {
        exchangeRateRepository.optInExchangeRateUsd(optIn)
    }

    fun dismissOptInExchangeRateUsd() {
        exchangeRateRepository.dismissOptInExchangeRateUsd()
    }

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

    fun persistNewWalletAndRestoringState(state: WalletRestoringState) {
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

            StandardPreferenceKeys.WALLET_RESTORING_STATE.putValue(
                standardPreferenceProvider(),
                state.toNumber()
            )
        }
    }

    /**
     * Asynchronously notes that the user has completed the backup steps, which means the wallet
     * is ready to use.  Clients observe [secretState] to see the side effects.  This would be used
     * for a user creating a new wallet.
     */
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
                standardPreferenceProvider(),
                walletRestoringState.toNumber()
            )
        }
    }

    fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    ) {
        walletRepository.persistExistingWalletWithSeedPhrase(network, seedPhrase, birthday)
    }

    private fun clearAppStateFlow(): Flow<Boolean> =
        callbackFlow {
            viewModelScope.launch {
                val standardPrefsCleared =
                    standardPreferenceProvider()
                        .clearPreferences()
                val encryptedPrefsCleared =
                    encryptedPreferenceProvider()
                        .clearPreferences()
                resetAddressBook()

                Twig.info { "Both preferences cleared: ${standardPrefsCleared && encryptedPrefsCleared}" }

                trySend(standardPrefsCleared && encryptedPrefsCleared)
            }

            awaitClose {
                // Nothing to close here
            }
        }

    fun deleteWallet(
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) = viewModelScope.launch(Dispatchers.Main) {
        Twig.info { "Delete wallet: Requested" }
        disconnectFlexa()

        getSynchronizer.getSdkSynchronizer()?.closeFlow()?.first()
        Twig.info { "Delete wallet: SDK closed" }
        val isSdkErased = walletCoordinator.deleteSdkDataFlow().first()
        Twig.info { "Delete wallet: Erase SDK result: $isSdkErased" }

        if (!isSdkErased) {
            Twig.error { "Wallet deletion failed" }
            onError()
            return@launch
        }

        val isAppErased = clearAppStateFlow().first()
        Twig.info { "Delete wallet: Erase App result: $isAppErased" }
        if (isAppErased) {
            Twig.info { "Wallet deleted successfully" }
            onSuccess()
        } else {
            Twig.error { "Wallet deletion failed" }
            onError()
        }
    }

    private suspend inline fun disconnectFlexa() {
        if (isFlexaAvailable()) {
            Flexa.buildIdentity().build().disconnect()
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

/**
 * This constant sets the default limitation on the length of the stack trace in the [SynchronizerError]
 */
const val STACKTRACE_LIMIT = 250

// TODO [#529]: Localize Synchronizer Errors
// TODO [#529]: https://github.com/Electric-Coin-Company/zashi-android/issues/529

/**
 * Represents all kind of Synchronizer errors
 */
sealed class SynchronizerError {
    abstract fun getCauseMessage(): String?

    abstract fun getStackTrace(limit: Int? = STACKTRACE_LIMIT): String?

    internal fun Throwable.stackTraceFullString() = stackTraceToString()

    internal fun Throwable.stackTraceToLimitedString(limit: Int) =
        if (stackTraceToString().isNotEmpty()) {
            stackTraceToString().substring(0..(stackTraceToString().length - 1).coerceAtMost(limit))
        } else {
            null
        }

    class Critical(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Processor(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Submission(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Setup(val error: Throwable?) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Chain(val x: BlockHeight, val y: BlockHeight) : SynchronizerError() {
        override fun getCauseMessage(): String = "$x, $y"

        override fun getStackTrace(limit: Int?): String? = null
    }
}

fun Synchronizer.Status.isSyncing() = this == Synchronizer.Status.SYNCING

fun Synchronizer.Status.isSynced() = this == Synchronizer.Status.SYNCED
