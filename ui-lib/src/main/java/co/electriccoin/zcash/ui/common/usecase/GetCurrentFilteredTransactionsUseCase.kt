package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class GetCurrentFilteredTransactionsUseCase(
    private val metadataRepository: MetadataRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionFilterRepository: TransactionFilterRepository,
    private val fulltextFilterUseCase: GetTransactionFulltextFiltersUseCase,
    private val restoreTimestampDataSource: RestoreTimestampDataSource
) {
    suspend operator fun invoke() = observe().filterNotNull().first()

    @Suppress("DestructuringDeclarationWithTooManyEntries")
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        combine(
            transactionRepository.currentTransactions,
            metadataRepository.metadata.filterNotNull(),
        ) { transactions, metadata ->
            transactions to metadata
        }.flatMapLatest { (transactions, metadata) ->
            combine(
                transactionFilterRepository.filters,
                transactionFilterRepository.fulltextFilter.map { it.orEmpty() }
            ) { filters, fullTextFilters ->
                filters to fullTextFilters
            }.flatMapLatest { (filters, fullTextFilters) ->
                val filteredTransactions =
                    transactions?.run {
                        filter { transaction ->
                            filterBySentReceived(filters, transaction)
                        }
                            .filter { transaction ->
                                filterByGeneralFilters(
                                    filters = filters,
                                    transaction = transaction,
                                    metadata = metadata,
                                    restoreTimestamp = restoreTimestampDataSource.getOrCreate()
                                )
                            }
                    }
                val fullTextFilteredTransactions =
                    filteredTransactions?.filter {
                        if (fullTextFilters.isNotEmpty()) {
                            fulltextFilterUseCase(it.overview.rawId)
                        } else {
                            true
                        }
                    }
                flow { emit(fullTextFilteredTransactions) }
            }
        }.distinctUntilChanged()

    private fun filterByGeneralFilters(
        filters: List<TransactionFilter>,
        transaction: TransactionData,
        metadata: Metadata,
        restoreTimestamp: Instant,
    ): Boolean {
        val memoPass = if (filters.contains(TransactionFilter.MEMOS)) {
            transaction.overview.memoCount > 0
        } else {
            true
        }
        val unreadPass = if (filters.contains(TransactionFilter.UNREAD)) {
            isUnread(metadata, transaction, restoreTimestamp)
        } else {
            true
        }
        val bookmarkPass = if (filters.contains(TransactionFilter.BOOKMARKED)) {
            isBookmark(metadata, transaction)
        } else {
            true
        }
        val notesPass = if (filters.contains(TransactionFilter.NOTES)) {
            hasNotes(metadata, transaction)
        } else {
            true
        }

        return memoPass && unreadPass && bookmarkPass && notesPass
    }

    @Suppress
    private fun filterBySentReceived(
        filters: List<TransactionFilter>,
        transaction: TransactionData
    ): Boolean {
        return if (filters.contains(TransactionFilter.SENT) || filters.contains(TransactionFilter.RECEIVED)) {
            when {
                filters.contains(TransactionFilter.SENT) &&
                    transaction.overview.isSentTransaction &&
                    !transaction.overview.isShielding -> true

                filters.contains(TransactionFilter.RECEIVED) &&
                    !transaction.overview.isSentTransaction &&
                    !transaction.overview.isShielding -> true

                else -> false
            }
        } else {
            true
        }
    }

    private fun isUnread(
        metadata: Metadata,
        transaction: TransactionData,
        restoreTimestamp: Instant,
    ): Boolean {
        val transactionDate =
            transaction.overview.blockTimeEpochSeconds
                ?.let { blockTimeEpochSeconds ->
                    Instant.ofEpochSecond(blockTimeEpochSeconds).atZone(ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()

        val hasMemo = transaction.overview.memoCount > 0
        val restoreDate = restoreTimestamp.atZone(ZoneId.systemDefault()).toLocalDate()

        return if (hasMemo && transactionDate < restoreDate) {
            false
        } else {
            val transactionMetadata =
                metadata.transactions
                    .find {
                        it.txId == transaction.overview.txIdString()
                    }

            hasMemo && (transactionMetadata == null || transactionMetadata.isMemoRead.not())
        }
    }

    private fun isBookmark(
        metadata: Metadata?,
        transaction: TransactionData
    ) = metadata?.transactions
        ?.find {
            it.txId == transaction.overview.txIdString()
        }
        ?.isBookmark ?: false

    private fun hasNotes(
        metadata: Metadata?,
        transaction: TransactionData
    ) = metadata?.transactions
        ?.find {
            it.txId == transaction.overview.txIdString()
        }
        ?.notes?.isNotEmpty() ?: false
}
