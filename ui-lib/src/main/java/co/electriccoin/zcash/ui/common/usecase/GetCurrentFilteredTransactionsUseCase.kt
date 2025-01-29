package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.util.Quadruple
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class GetCurrentFilteredTransactionsUseCase(
    private val metadataRepository: MetadataRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionFilterRepository: TransactionFilterRepository,
) {
    suspend operator fun invoke() = observe().filterNotNull().first()

    @Suppress("DestructuringDeclarationWithTooManyEntries")
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        combine(
            transactionRepository.currentTransactions,
            transactionFilterRepository.filters,
            metadataRepository.metadata.filterNotNull(),
            transactionFilterRepository.fulltextFilter.map { it.orEmpty() }
        ) { transactions, filters, metadata, fulltextFilter ->
            Quadruple(transactions, filters, metadata, fulltextFilter)
        }.mapLatest { (transactions, filters, metadata, _) ->
            transactions
                ?.filter { transaction ->
                    filterBySentReceived(filters, transaction)
                }
                ?.filter { transaction ->
                    filterByGeneralFilters(filters, transaction, metadata)
                }
        }

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
