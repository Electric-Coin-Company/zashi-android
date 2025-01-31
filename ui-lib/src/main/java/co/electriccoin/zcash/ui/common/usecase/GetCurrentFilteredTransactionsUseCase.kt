package co.electriccoin.zcash.ui.common.usecase

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

class GetCurrentFilteredTransactionsUseCase(
    private val metadataRepository: MetadataRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionFilterRepository: TransactionFilterRepository,
    private val fulltextFilterUseCase: GetTransactionFulltextFiltersUseCase
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
                                filterByGeneralFilters(filters, transaction, metadata)
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
        metadata: Metadata
    ): Boolean {
        val memoPass = if (filters.contains(TransactionFilter.MEMOS)) transaction.overview.memoCount > 0 else true
        val unreadPass = if (filters.contains(TransactionFilter.UNREAD)) isUnread(metadata, transaction) else true
        val bookmarkPass =
            if (filters.contains(TransactionFilter.BOOKMARKED)) isBookmark(metadata, transaction) else true
        val notesPass = if (filters.contains(TransactionFilter.NOTES)) hasNotes(metadata, transaction) else true

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
        transaction: TransactionData
    ): Boolean {
        val hasMemo = transaction.overview.memoCount > 0
        val transactionMetadata =
            metadata.transactions
                .find {
                    it.txId == transaction.overview.txIdString()
                }
        return hasMemo && (transactionMetadata == null || transactionMetadata.isMemoRead.not())
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
