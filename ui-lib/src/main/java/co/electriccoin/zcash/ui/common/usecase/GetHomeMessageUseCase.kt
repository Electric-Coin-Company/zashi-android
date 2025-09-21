package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.common.datasource.WalletBackupData
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource
import co.electriccoin.zcash.ui.common.datasource.WalletSnapshotDataSource
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.CrashReportingStorageProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageCacheRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.RuntimeMessage
import co.electriccoin.zcash.ui.common.repository.ShieldFundsData
import co.electriccoin.zcash.ui.common.repository.ShieldFundsRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class GetHomeMessageUseCase(
    walletBackupDataSource: WalletBackupDataSource,
    exchangeRateRepository: ExchangeRateRepository,
    shieldFundsRepository: ShieldFundsRepository,
    transactionRepository: TransactionRepository,
    walletSnapshotDataSource: WalletSnapshotDataSource,
    crashReportingStorageProvider: CrashReportingStorageProvider,
    synchronizerProvider: SynchronizerProvider,
    accountDataSource: AccountDataSource,
    private val messageAvailabilityDataSource: MessageAvailabilityDataSource,
    private val cache: HomeMessageCacheRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val backupFlow =
        combine(
            accountDataSource.selectedAccount,
            transactionRepository.currentTransactions,
            walletBackupDataSource.observe()
        ) { account, transactions, backup ->
            if (backup is WalletBackupData.Available &&
                account is ZashiAccount &&
                transactions.orEmpty().any { it is ReceiveTransaction }
            ) {
                backup
            } else {
                WalletBackupData.Unavailable
            }
        }.distinctUntilChanged()

    @Suppress("MagicNumber")
    private val runtimeMessage =
        channelFlow {
            var firstSyncingMessage: HomeMessageData.Syncing? = null
            combine(
                shieldFundsRepository.availability,
                walletSnapshotDataSource.observe()
            ) { availability, walletSnapshot ->
                availability to walletSnapshot
            }.collect { (availability, walletSnapshot) ->
                if (walletSnapshot == null) {
                    send(null)
                    return@collect
                }

                val message = createSynchronizerErrorMessage(walletSnapshot)
                    ?: createDisconnectedMessage(walletSnapshot)
                    ?: createSyncingMessage(walletSnapshot, syncMessageShownBefore = firstSyncingMessage != null)
                    ?: createShieldFundsMessage(availability)

                if (message is HomeMessageData.Syncing && firstSyncingMessage == null) {
                    firstSyncingMessage = message
                }

                send(message)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isExchangeRateMessageVisible =
        exchangeRateRepository.state
            .flatMapLatest {
                val isVisible = it == ExchangeRateState.OptIn

                synchronizerProvider.synchronizer.map { synchronizer ->
                    synchronizer != null && isVisible
                }
            }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isCrashReportMessageVisible =
        crashReportingStorageProvider
            .observe()
            .flatMapLatest { isVisible ->
                synchronizerProvider.synchronizer.flatMapLatest { synchronizer ->
                    synchronizer
                        ?.status
                        ?.map {
                            it != Synchronizer.Status.INITIALIZING && isVisible == null
                        } ?: flowOf(false)
                }
            }.distinctUntilChanged()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val flow =
        combine(
            synchronizerProvider.synchronizer.flatMapLatest { it?.status ?: flowOf(null) },
            runtimeMessage,
            backupFlow,
            isExchangeRateMessageVisible,
            isCrashReportMessageVisible,
        ) { status, runtimeMessage, backup, isCCAvailable, isCrashReportingEnabled ->
            createMessage(
                status = status,
                runtimeMessage = runtimeMessage,
                backup = backup,
                isCurrencyConversionEnabled = isCCAvailable,
                isCrashReportingVisible = isCrashReportingEnabled,
            )
        }.distinctUntilChanged()
            .debounce(1.seconds)
            .map { message -> prioritizeMessage(message) }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(0, 0),
                initialValue = null
            )

    fun observe(): StateFlow<HomeMessageData?> = flow

    private fun createMessage(
        status: Synchronizer.Status?,
        runtimeMessage: RuntimeMessage?,
        backup: WalletBackupData,
        isCurrencyConversionEnabled: Boolean,
        isCrashReportingVisible: Boolean,
    ) = when {
        status == null || status == Synchronizer.Status.INITIALIZING -> null
        runtimeMessage != null -> runtimeMessage
        backup is WalletBackupData.Available -> HomeMessageData.Backup
        isCurrencyConversionEnabled -> HomeMessageData.EnableCurrencyConversion
        isCrashReportingVisible -> HomeMessageData.CrashReport
        else -> null
    }

    private fun prioritizeMessage(message: HomeMessageData?): HomeMessageData? {
        val isSameMessageUpdate = message?.priority == cache.lastMessage?.priority // same but updated
        val someMessageBeenShown = cache.lastShownMessage != null // has any message been shown while app in fg
        val hasNoMessageBeenShownLately = cache.lastMessage == null // has no message been shown
        val isHigherPriorityMessage = (message?.priority ?: 0) > (cache.lastShownMessage?.priority ?: 0)
        val result =
            when {
                message == null -> null
                message is RuntimeMessage -> message
                isSameMessageUpdate -> message
                isHigherPriorityMessage ->
                    if (hasNoMessageBeenShownLately) {
                        if (someMessageBeenShown) null else message
                    } else {
                        message
                    }

                else -> null
            }

        if (result != null) {
            messageAvailabilityDataSource.onMessageShown()
            cache.lastShownMessage = result
        }
        cache.lastMessage = result

        Twig.debug {
            when {
                message == null -> "Home message: no message to show"
                result == null -> "Home message: ${message::class.simpleName} was filtered out"
                else -> "Home message: ${result::class.simpleName} shown"
            }
        }

        return result
    }


    private fun createSynchronizerErrorMessage(walletSnapshot: WalletSnapshot): HomeMessageData.Error? {
        if (walletSnapshot.synchronizerError == null ||
            (walletSnapshot.synchronizerError is SynchronizerError.Processor &&
                walletSnapshot.synchronizerError.error is CancellationException)
        ) return null

        return HomeMessageData.Error(walletSnapshot.synchronizerError)
    }

    private fun createDisconnectedMessage(walletSnapshot: WalletSnapshot): HomeMessageData.Disconnected? {
        return if (walletSnapshot.status == Synchronizer.Status.DISCONNECTED) {
            HomeMessageData.Disconnected
        } else {
            null
        }
    }

    private fun createSyncingMessage(
        walletSnapshot: WalletSnapshot,
        syncMessageShownBefore: Boolean
    ): RuntimeMessage? {
        if (walletSnapshot.status != Synchronizer.Status.SYNCING) return null

        val progress = walletSnapshot.progress.decimal * 100f
        return if (walletSnapshot.restoringState == WalletRestoringState.RESTORING) {
            HomeMessageData.Restoring(walletSnapshot.isSpendable, progress)
        } else {
            if (syncMessageShownBefore) {
                if (progress >= .95f) null else HomeMessageData.Syncing(progress = progress)
            } else {
                HomeMessageData.Syncing(progress = progress)
            }
        }
    }

    private fun createShieldFundsMessage(availability: ShieldFundsData): HomeMessageData.ShieldFunds? {
        if (availability !is ShieldFundsData.Available) return null
        return HomeMessageData.ShieldFunds(zatoshi = availability.amount)
    }

}

@Suppress("UNCHECKED_CAST", "MagicNumber")
private fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6) { args ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6
        )
    }
