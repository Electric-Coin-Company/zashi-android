package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.TransactionOutput
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVE_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SEND_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENT
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING_FAILED
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface TransactionRepository {
    val currentTransactions: Flow<List<TransactionData>?>

    suspend fun getMemos(transactionData: TransactionData): List<String>

    suspend fun getRecipients(transactionData: TransactionData): String?
}

class TransactionRepositoryImpl(
    accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
) : TransactionRepository {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentTransactions: Flow<List<TransactionData>?> =
        combine(
            synchronizerProvider.synchronizer,
            accountDataSource.selectedAccount.map { it?.sdkAccount }
        ) { synchronizer, account ->
            synchronizer to account
        }.distinctUntilChanged().flatMapLatest { (synchronizer, account) ->
            if (synchronizer == null || account == null) {
                flowOf(null)
            } else {
                channelFlow<List<TransactionData>?> {
                    send(null)

                    launch {
                        synchronizer.getTransactions(account.accountUuid)
                            .flatMapLatest { transactions ->
                                synchronizer.networkHeight.mapLatest {
                                    transactions to it
                                }
                            }
                            .map { (transactions, networkHeight) ->
                                transactions.map { transaction ->
                                    TransactionData(
                                        overview = transaction,
                                        transactionOutputs = synchronizer.getTransactionOutputs(transaction),
                                        state = transaction.getExtendedState()
                                    )
                                }.sortedByDescending { transaction ->
                                    transaction.overview.getSortHeight(networkHeight)
                                }
                            }
                            .collect {
                                send(it)
                            }
                    }

                    awaitClose {
                        // do nothing
                    }
                }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5.seconds, Duration.ZERO),
            initialValue = null
        )

    override suspend fun getMemos(transactionData: TransactionData): List<String> {
        return synchronizerProvider.getSynchronizer().getMemos(transactionData.overview)
            .mapNotNull { memo -> memo.takeIf { it.isNotEmpty() } }
            .toList()
    }

    override suspend fun getRecipients(transactionData: TransactionData): String? {
        if (transactionData.overview.isSentTransaction) {
            val result = synchronizerProvider.getSynchronizer().getRecipients(transactionData.overview).firstOrNull()
            return (result as? TransactionRecipient.RecipientAddress)?.addressValue
        } else {
            return null
        }
    }

    private fun TransactionOverview.getSortHeight(networkHeight: BlockHeight?): BlockHeight? {
        // Non-null assertion operator is necessary here as the smart cast to is impossible because `minedHeight` and
        // `expiryHeight` are declared in a different module
        return when {
            minedHeight != null -> minedHeight!!
            (expiryHeight?.value ?: 0) > 0 -> expiryHeight!!
            else -> networkHeight
        }
    }
}

data class TransactionData(
    val overview: TransactionOverview,
    val transactionOutputs: List<TransactionOutput>,
    val state: TransactionExtendedState,
)

enum class TransactionExtendedState {
    SENT,
    SENDING,
    SEND_FAILED,
    RECEIVED,
    RECEIVING,
    RECEIVE_FAILED,
    SHIELDED,
    SHIELDING,
    SHIELDING_FAILED
}

private fun TransactionOverview.getExtendedState(): TransactionExtendedState {
    return when (transactionState) {
        cash.z.ecc.android.sdk.model.TransactionState.Expired ->
            when {
                isShielding -> SHIELDING_FAILED
                isSentTransaction -> SEND_FAILED
                else -> RECEIVE_FAILED
            }

        cash.z.ecc.android.sdk.model.TransactionState.Confirmed ->
            when {
                isShielding -> SHIELDED
                isSentTransaction -> SENT
                else -> RECEIVED
            }

        cash.z.ecc.android.sdk.model.TransactionState.Pending ->
            when {
                isShielding -> SHIELDING
                isSentTransaction -> SENDING
                else -> RECEIVING
            }

        else -> error("Unexpected transaction state found while calculating its extended state.")
    }
}
