package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.AccountUuid
import cash.z.ecc.android.sdk.model.TransactionId
import cash.z.ecc.android.sdk.model.TransactionOutput
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionState.Confirmed
import cash.z.ecc.android.sdk.model.TransactionState.Expired
import cash.z.ecc.android.sdk.model.TransactionState.Pending
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface TransactionRepository {
    val zashiTransactions: Flow<List<Transaction>?>

    val currentTransactions: Flow<List<Transaction>?>

    suspend fun getMemos(transaction: Transaction): List<String>

    suspend fun getRecipients(transaction: Transaction): String?

    fun observeTransaction(txId: String): Flow<Transaction?>

    fun observeTransactionsByMemo(memo: String): Flow<List<TransactionId>?>

    suspend fun getTransactions(): List<Transaction>
}

class TransactionRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
) : TransactionRepository {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override val zashiTransactions: Flow<List<Transaction>?> =
        observeTransactions(
            accountFlow = accountDataSource.zashiAccount.map { it?.sdkAccount?.accountUuid }.distinctUntilChanged()
        ).stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5.seconds, Duration.ZERO),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentTransactions: Flow<List<Transaction>?> = accountDataSource.selectedAccount
        .distinctUntilChangedBy { it?.sdkAccount?.accountUuid }
        .flatMapLatest { selected ->
            if (selected is ZashiAccount) {
                zashiTransactions
            } else {
                observeTransactions(
                    accountFlow = accountDataSource.selectedAccount.map { it?.sdkAccount?.accountUuid }
                        .distinctUntilChanged()
                )
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5.seconds, Duration.ZERO),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TransactionRepositoryImpl.observeTransactions(accountFlow: Flow<AccountUuid?>) =
        combine(
            synchronizerProvider.synchronizer,
            accountFlow
        ) { synchronizer, account ->
            synchronizer to account
        }.distinctUntilChanged()
            .flatMapLatest { (synchronizer, accountUuid) ->
                if (synchronizer == null || accountUuid == null) {
                    flowOf(null)
                } else {
                    channelFlow<List<Transaction>?> {
                        send(null)

                        launch {
                            synchronizer
                                .getTransactions(accountUuid)
                                .mapLatest { transactions ->
                                    createTransactions(transactions = transactions, synchronizer = synchronizer)
                                }.collect {
                                    send(it)
                                }
                        }

                        awaitClose {
                            // do nothing
                        }
                    }
                }
            }

    private suspend fun createTransactions(
        transactions: List<TransactionOverview>,
        synchronizer: Synchronizer
    ) = transactions
        .map { transaction ->
            when (transaction.transactionState) {
                Expired ->
                    when {
                        transaction.isShielding ->
                            ShieldTransaction.Failed(
                                timestamp =
                                    createTimestamp(transaction) ?: Instant.now(),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.totalSpent,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                fee = transaction.netValue,
                                overview = transaction
                            )

                        transaction.isSentTransaction ->
                            SendTransaction.Failed(
                                timestamp =
                                    createTimestamp(transaction) ?: Instant.now(),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.netValue,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                fee = transaction.feePaid,
                                overview = transaction
                            )

                        else ->
                            ReceiveTransaction.Failed(
                                timestamp =
                                    createTimestamp(transaction) ?: Instant.now(),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs(transaction),
                                amount = transaction.netValue,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                overview = transaction
                            )
                    }

                Confirmed ->
                    when {
                        transaction.isShielding ->
                            ShieldTransaction.Success(
                                timestamp =
                                    createTimestamp(transaction) ?: Instant.now(),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.totalSpent,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                fee = transaction.netValue,
                                overview = transaction
                            )

                        transaction.isSentTransaction ->
                            SendTransaction.Success(
                                timestamp =
                                    createTimestamp(transaction) ?: Instant.now(),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.netValue,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                fee = transaction.feePaid,
                                overview = transaction
                            )

                        else ->
                            ReceiveTransaction.Success(
                                timestamp =
                                    createTimestamp(transaction) ?: Instant.now(),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.netValue,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                overview = transaction
                            )
                    }

                Pending ->
                    when {
                        transaction.isShielding ->
                            ShieldTransaction.Pending(
                                timestamp = createTimestamp(transaction),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.totalSpent,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                fee = transaction.netValue,
                                overview = transaction
                            )

                        transaction.isSentTransaction ->
                            SendTransaction.Pending(
                                timestamp = createTimestamp(transaction),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.netValue,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                fee = transaction.feePaid,
                                overview = transaction
                            )

                        else ->
                            ReceiveTransaction.Pending(
                                timestamp = createTimestamp(transaction),
                                transactionOutputs =
                                    synchronizer.getTransactionOutputs
                                        (transaction),
                                amount = transaction.netValue,
                                id = transaction.txId,
                                memoCount = transaction.memoCount,
                                overview = transaction
                            )
                    }

                else -> error("Unexpected transaction stat")
            }
        }.sortedByDescending { transaction ->
            transaction.timestamp ?: Instant.now()
        }

    private fun createTimestamp(transaction: TransactionOverview): Instant? =
        transaction.blockTimeEpochSeconds?.let {
            Instant.ofEpochSecond(it)
        }

    override suspend fun getMemos(transaction: Transaction): List<String> =
        withContext(Dispatchers.IO) {
            synchronizerProvider
                .getSynchronizer()
                .getMemos(transaction.overview)
                .mapNotNull { memo -> memo.takeIf { it.isNotEmpty() } }
                .toList()
        }

    override suspend fun getRecipients(transaction: Transaction): String? =
        withContext(Dispatchers.IO) {
            if (transaction is SendTransaction) {
                synchronizerProvider
                    .getSynchronizer()
                    .getRecipients(transaction.overview)
                    .firstOrNull()
                    ?.addressValue
            } else {
                null
            }
        }

    override fun observeTransaction(txId: String): Flow<Transaction?> =
        currentTransactions
            .map { transactions ->
                transactions?.find { it.id.txIdString() == txId }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeTransactionsByMemo(memo: String): Flow<List<TransactionId>?> =
        synchronizerProvider
            .synchronizer
            .flatMapLatest { synchronizer ->
                synchronizer?.getTransactionsByMemoSubstring(memo)?.onEmpty { emit(listOf()) } ?: flowOf(null)
            }.distinctUntilChanged()

    override suspend fun getTransactions(): List<Transaction> = currentTransactions.filterNotNull().first()
}

sealed interface Transaction {
    val id: TransactionId
    val amount: Zatoshi
    val memoCount: Int
    val timestamp: Instant?
    val transactionOutputs: List<TransactionOutput>
    val overview: TransactionOverview
    val fee: Zatoshi?
}

sealed interface SendTransaction : Transaction {
    data class Success(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant,
        override val memoCount: Int,
        override val fee: Zatoshi?,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : SendTransaction

    data class Pending(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant?,
        override val memoCount: Int,
        override val fee: Zatoshi?,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : SendTransaction

    data class Failed(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant,
        override val memoCount: Int,
        override val fee: Zatoshi?,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : SendTransaction
}

sealed interface ReceiveTransaction : Transaction {
    override val fee: Zatoshi?
        get() = null

    data class Success(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant,
        override val memoCount: Int,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : ReceiveTransaction

    data class Pending(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant?,
        override val memoCount: Int,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : ReceiveTransaction

    data class Failed(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant,
        override val memoCount: Int,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : ReceiveTransaction
}

sealed interface ShieldTransaction : Transaction {
    data class Success(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant,
        override val memoCount: Int,
        override val fee: Zatoshi?,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : ShieldTransaction

    data class Pending(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val timestamp: Instant?,
        override val memoCount: Int,
        override val fee: Zatoshi?,
        override val transactionOutputs: List<TransactionOutput>,
        override val overview: TransactionOverview,
    ) : ShieldTransaction

    data class Failed(
        override val id: TransactionId,
        override val amount: Zatoshi,
        override val memoCount: Int,
        override val timestamp: Instant,
        override val transactionOutputs: List<TransactionOutput>,
        override val fee: Zatoshi?,
        override val overview: TransactionOverview,
    ) : ShieldTransaction
}
