package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.ZecSimpleSwapAsset
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.common.repository.TransactionSwapMetadata
import co.electriccoin.zcash.ui.design.util.combineToFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.collections.filter

class GetActivitiesUseCase(
    private val transactionRepository: TransactionRepository,
    private val metadataRepository: MetadataRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        combine(observeTransactions(), observeIntoZecSwaps()) { transactions, swaps ->
            if (transactions == null || swaps == null) {
                null
            } else {
                transactions
                    .sortedWith(
                        compareByDescending<ActivityData.ByTransaction> { it.timestamp }
                            .thenByDescending { it.transaction.id.txIdString() }
                    ) + swaps
            }
        }.map {
            val endOfDay =
                LocalDateTime
                    .of(LocalDate.now(), LocalTime.MAX)
                    .toInstant(ZoneOffset.UTC)

            it?.sortedByDescending { activity ->
                when (activity) {
                    is ActivityData.ByTransaction -> activity.transaction.timestamp ?: endOfDay
                    is ActivityData.BySwap -> activity.swap.lastUpdated
                }
            }
        }.flowOn(Dispatchers.Default)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTransactions(): Flow<List<ActivityData.ByTransaction>?> =
        transactionRepository.currentTransactions
            .flatMapLatest { transactions ->
                transactions
                    ?.map {
                    metadataRepository
                        .observeTransactionMetadata(it)
                        .mapLatest { metadata ->
                            ActivityData.ByTransaction(transaction = it, metadata = metadata)
                        }
                }?.combineToFlow() ?: flowOf(null)
            }

    private fun observeIntoZecSwaps(): Flow<List<ActivityData.BySwap>?> =
        metadataRepository
            .observeSwapMetadata()
            .map {
                it?.filter { metadata -> metadata.destination is ZecSimpleSwapAsset }
            }
            .map {
                it?.map { metadata -> ActivityData.BySwap(swap = metadata) }
            }
}

sealed interface ActivityData {
    val timestamp: Instant?

    data class ByTransaction(
        val transaction: Transaction,
        val metadata: TransactionMetadata
    ) : ActivityData {
        override val timestamp = transaction.timestamp
    }

    data class BySwap(
        val swap: TransactionSwapMetadata
    ) : ActivityData {
        override val timestamp = swap.lastUpdated
    }
}
