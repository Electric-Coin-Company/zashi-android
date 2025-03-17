package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.combineToFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class GetCurrentTransactionsUseCase(
    private val transactionRepository: TransactionRepository,
    private val metadataRepository: MetadataRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        transactionRepository.currentTransactions
            .flatMapLatest { transactions ->
                if (transactions == null) {
                    flowOf(null)
                } else if (transactions.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    transactions
                        .map {
                            metadataRepository.observeTransactionMetadataByTxId(it.id.txIdString())
                                .mapLatest { metadata ->
                                    ListTransactionData(
                                        transaction = it,
                                        metadata = metadata
                                    )
                                }
                        }
                        .combineToFlow()
                }
            }
}

data class ListTransactionData(
    val transaction: Transaction,
    val metadata: TransactionMetadata?
)
