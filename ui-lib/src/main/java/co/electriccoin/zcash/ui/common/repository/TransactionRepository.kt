package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.TransactionOutput
import cash.z.ecc.android.sdk.model.TransactionOverview
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

interface TransactionRepository {
    val currentTransactions: Flow<List<TransactionData>?>
}

class TransactionRepositoryImpl(
    synchronizerProvider: SynchronizerProvider,
    accountDataSource: AccountDataSource
) : TransactionRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentTransactions: Flow<List<TransactionData>?> =
        combine(
            synchronizerProvider.synchronizer,
            synchronizerProvider.synchronizer.flatMapLatest { it?.networkHeight ?: flowOf(null) },
            accountDataSource.selectedAccount.map { it?.sdkAccount }
        ) { synchronizer, networkHeight, account ->
            Triple(synchronizer, networkHeight, account)
        }
            .flatMapLatest { (synchronizer, networkHeight, account) ->
                if (synchronizer == null || account == null) {
                    flowOf(null)
                } else {
                    synchronizer.getTransactions(account.accountUuid)
                        .map {
                            it.map { transaction ->
                                TransactionData(
                                    transactionOverview = transaction,
                                    transactionOutputs = synchronizer.getTransactionOutputs(transaction),
                                )
                            }.sortedByDescending { transaction ->
                                transaction.transactionOverview.getSortHeight(networkHeight)
                            }
                        }
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(Duration.ZERO, Duration.ZERO),
                initialValue = null
            )

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
    val transactionOverview: TransactionOverview,
    val transactionOutputs: List<TransactionOutput>,
)
