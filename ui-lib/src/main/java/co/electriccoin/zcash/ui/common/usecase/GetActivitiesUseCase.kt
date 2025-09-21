package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.SwapMetadata
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.combineToFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import java.time.Instant

class GetActivitiesUseCase(
    private val transactionRepository: TransactionRepository,
    private val metadataRepository: MetadataRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        combine(observeTransactions(), observeSwaps()) { transactions, swaps ->
            if (transactions == null || swaps == null) {
                null
            } else {
                transactions + swaps
            }
        }.map {
            it?.sortedByDescending { activity ->
                when (activity) {
                    is ActivityData.ByTransaction -> activity.transaction.timestamp
                    is ActivityData.BySwap -> activity.swap.lastUpdated
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTransactions(): Flow<List<ActivityData>?> =
        transactionRepository.currentTransactions
            .flatMapLatest { transactions ->
                if (transactions == null) {
                    flowOf(null)
                } else if (transactions.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    transactions
                        .map {
                            metadataRepository
                                .observeTransactionMetadata(it)
                                .mapLatest { metadata ->
                                    ActivityData.ByTransaction(transaction = it, metadata = metadata)
                                }
                        }.combineToFlow()
                }
            }

    private fun observeSwaps() =
        metadataRepository
            .observeORSwapMetadata()
            .map {
                it?.map { metadata ->
                    ActivityData.BySwap(swap = metadata)
                }
            }
}

sealed interface ActivityData {

    val timestamp: Instant?

    data class ByTransaction(val transaction: Transaction, val metadata: TransactionMetadata) : ActivityData {
        override val timestamp = transaction.timestamp
    }

    data class BySwap(val swap: SwapMetadata) : ActivityData {
        override val timestamp = swap.lastUpdated
    }
}
