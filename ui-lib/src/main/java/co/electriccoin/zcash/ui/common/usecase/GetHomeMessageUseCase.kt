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
import co.electriccoin.zcash.ui.common.provider.TorState
import co.electriccoin.zcash.ui.common.repository.HomeMessageCacheRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.RuntimeMessage
import co.electriccoin.zcash.ui.common.repository.ShieldFundsData
import co.electriccoin.zcash.ui.common.repository.ShieldFundsRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
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
            var firstSyncing: WalletSnapshot? = null
            launch {
                walletSnapshotDataSource
                    .observe()
                    .collect { walletSnapshot ->
                        val result =
                            when {
                                walletSnapshot == null -> null
                                walletSnapshot.synchronizerError != null ->
                                    HomeMessageData.Error(walletSnapshot.synchronizerError)

                                walletSnapshot.status == Synchronizer.Status.DISCONNECTED ->
                                    HomeMessageData.Disconnected

                                walletSnapshot.status in
                                    listOf(
                                        Synchronizer.Status.SYNCING,
                                        Synchronizer.Status.STOPPED
                                    ) -> {
                                    val progress = walletSnapshot.progress.decimal * 100f
                                    if (walletSnapshot.restoringState == WalletRestoringState.RESTORING) {
                                        HomeMessageData.Restoring(
                                            isSpendable = walletSnapshot.isSpendable,
                                            progress = progress
                                        )
                                    } else {
                                        HomeMessageData.Syncing(progress = progress)
                                    }
                                }

                                else -> null
                            }

                        if (result is HomeMessageData.Syncing) {
                            if (firstSyncing == null) {
                                firstSyncing = walletSnapshot
                            }

                            if ((firstSyncing?.progress?.decimal ?: 0f) >= .95f) {
                                send(null)
                            } else {
                                send(result)
                            }
                        } else {
                            firstSyncing = null
                            send(result)
                        }
                    }
            }

            awaitClose {
                // do nothing
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isCrashReportMessageVisible =
        crashReportingStorageProvider
            .observe()
            .flatMapLatest { isVisible ->
                synchronizerProvider.synchronizer.flatMapLatest { synchronizer ->
                    synchronizer?.status
                        ?.map {
                            it != Synchronizer.Status.INITIALIZING && isVisible == null
                        } ?: flowOf(false)
                }
            }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val torState = synchronizerProvider.synchronizer.flatMapLatest { synchronizer ->
        if (synchronizer == null) return@flatMapLatest flowOf(null)

        synchronizer.status.map { status ->
            if (status == Synchronizer.Status.INITIALIZING) {
                return@map null
            }
            when (synchronizer.flags.isTorEnabled) {
                true -> TorState.EXPLICITLY_ENABLED
                false -> TorState.EXPLICITLY_DISABLED
                null -> TorState.IMPLICITLY_DISABLED
            }
        }.distinctUntilChanged()
    }

    @OptIn(FlowPreview::class)
    private val flow =
        combine(
            runtimeMessage,
            backupFlow,
            shieldFundsRepository.availability,
            isCrashReportMessageVisible,
            torState,
        ) { runtimeMessage, backup, shieldFunds, isCrashReportingEnabled, torState ->
            createMessage(
                runtimeMessage = runtimeMessage,
                backup = backup,
                shieldFunds = shieldFunds,
                isCrashReportingVisible = isCrashReportingEnabled,
                torState = torState
            )
        }.distinctUntilChanged()
            .debounce(.5.seconds)
            .map { message -> prioritizeMessage(message) }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(0, 0),
                initialValue = null
            )

    fun observe(): StateFlow<HomeMessageData?> = flow

    private fun createMessage(
        runtimeMessage: RuntimeMessage?,
        backup: WalletBackupData,
        shieldFunds: ShieldFundsData,
        isCrashReportingVisible: Boolean,
        torState: TorState?
    ) = when {
        runtimeMessage != null -> runtimeMessage
        torState == TorState.IMPLICITLY_DISABLED -> HomeMessageData.EnableTor
        backup is WalletBackupData.Available -> HomeMessageData.Backup
        shieldFunds is ShieldFundsData.Available -> HomeMessageData.ShieldFunds(shieldFunds.amount)
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
                else -> {
                    "Home message: ${result::class.simpleName} shown"
                }
            }
        }

        return result
    }
}
