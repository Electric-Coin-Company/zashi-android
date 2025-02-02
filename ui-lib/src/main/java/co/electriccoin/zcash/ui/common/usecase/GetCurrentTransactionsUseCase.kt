package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.util.combineToFlow
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
                            metadataRepository.observeTransactionMetadataByTxId(it.overview.txId.txIdString())
                                .mapLatest { metadata ->
                                    ListTransactionData(
                                        data = it,
                                        metadata = metadata
                                    )
                                }
                        }
                        .combineToFlow()
                }
            }
}

data class ListTransactionData(
    val data: TransactionData,
    val metadata: TransactionMetadata?
)
