package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import cash.z.ecc.android.sdk.model.TransactionId
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionMetadata
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.util.combineToFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.seconds

@Suppress("TooManyFunctions")
class GetCurrentFilteredTransactionsUseCase(
    transactionFilterRepository: TransactionFilterRepository,
    private val metadataRepository: MetadataRepository,
    private val transactionRepository: TransactionRepository,
    private val restoreTimestampDataSource: RestoreTimestampDataSource,
    private val addressBookRepository: AddressBookRepository,
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val detailedCurrentTransactions =
        transactionRepository.currentTransactions
            .flatMapLatest { transactions ->
                val enhancedTransactions =
                    transactions
                        ?.map { transaction ->
                            val recipient = transactionRepository.getRecipients(transaction)

                            if (recipient == null) {
                                metadataRepository.observeTransactionMetadataByTxId(
                                    transaction.overview.txId.txIdString()
                                ).map {
                                    FilterTransactionData(
                                        transaction = transaction,
                                        contact = null,
                                        recipientAddress = null,
                                        transactionMetadata = it
                                    )
                                }
                            } else {
                                combine(
                                    addressBookRepository.observeContactByAddress(recipient),
                                    metadataRepository.observeTransactionMetadataByTxId(
                                        txId = transaction.overview.txId.txIdString(),
                                    )
                                ) { contact, transactionMetadata ->
                                    FilterTransactionData(
                                        transaction = transaction,
                                        contact = contact,
                                        recipientAddress = recipient,
                                        transactionMetadata = transactionMetadata
                                    )
                                }
                            }
                        }

                enhancedTransactions?.combineToFlow() ?: flowOf(null)
            }
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds, 5.seconds),
                replay = 1
            )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val transactionsFilteredByFulltext: Flow<List<FilterTransactionData>?> =
        transactionFilterRepository
            .fulltextFilter
            .debounce(.69.seconds)
            .distinctUntilChanged()
            .flatMapLatest { fulltextFilter ->
                flow {
                    emit(null)

                    emitAll(
                        if (fulltextFilter == null || fulltextFilter.length < MIN_TEXT_FILTER_LENGTH) {
                            detailedCurrentTransactions
                        } else {
                            combine(
                                detailedCurrentTransactions,
                                transactionRepository.observeTransactionsByMemo(fulltextFilter)
                            ) { transactions, memoTxIds ->
                                transactions to memoTxIds
                            }.mapLatest { (transactions, memoTxIds) ->
                                transactions
                                    ?.filter { transaction ->
                                        hasMemoInFilteredIds(memoTxIds, transaction) ||
                                            hasContactInAddressBookWithFulltext(transaction, fulltextFilter) ||
                                            hasAddressWithFulltext(transaction, fulltextFilter) ||
                                            hasNotesWithFulltext(transaction, fulltextFilter) ||
                                            hasAmountWithFulltext(transaction, fulltextFilter)
                                    }
                            }
                        }
                    )
                }
            }
            .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val result =
        transactionFilterRepository.filters
            .flatMapLatest { filters ->
                flow {
                    emit(null)
                    emitAll(
                        transactionsFilteredByFulltext
                            .mapLatest { transactions ->
                                transactions
                                    ?.filter { transaction ->
                                        filterBySentReceived(filters, transaction)
                                    }
                                    ?.filter { transaction ->
                                        filterByGeneralFilters(
                                            filters = filters,
                                            transaction = transaction,
                                            restoreTimestamp = restoreTimestampDataSource.getOrCreate()
                                        )
                                    }
                                    ?.map { transaction ->
                                        ListTransactionData(
                                            data = transaction.transaction,
                                            metadata = transaction.transactionMetadata
                                        )
                                    }
                            }
                    )
                }
            }
            .distinctUntilChanged()

    fun observe() = result

    private fun filterByGeneralFilters(
        filters: List<TransactionFilter>,
        transaction: FilterTransactionData,
        restoreTimestamp: Instant,
    ): Boolean {
        val memoPass =
            if (filters.contains(TransactionFilter.MEMOS)) {
                transaction.transaction.overview.memoCount > 0
            } else {
                true
            }
        val unreadPass =
            if (filters.contains(TransactionFilter.UNREAD)) {
                isUnread(transaction, restoreTimestamp)
            } else {
                true
            }
        val bookmarkPass =
            if (filters.contains(TransactionFilter.BOOKMARKED)) {
                isBookmark(transaction)
            } else {
                true
            }
        val notesPass =
            if (filters.contains(TransactionFilter.NOTES)) {
                hasNotes(transaction)
            } else {
                true
            }

        return memoPass && unreadPass && bookmarkPass && notesPass
    }

    @Suppress
    private fun filterBySentReceived(
        filters: List<TransactionFilter>,
        transaction: FilterTransactionData
    ): Boolean {
        return if (filters.contains(TransactionFilter.SENT) || filters.contains(TransactionFilter.RECEIVED)) {
            when {
                filters.contains(TransactionFilter.SENT) &&
                    transaction.transaction.overview.isSentTransaction &&
                    !transaction.transaction.overview.isShielding -> true

                filters.contains(TransactionFilter.RECEIVED) &&
                    !transaction.transaction.overview.isSentTransaction &&
                    !transaction.transaction.overview.isShielding -> true

                else -> false
            }
        } else {
            true
        }
    }

    private fun isUnread(
        transaction: FilterTransactionData,
        restoreTimestamp: Instant,
    ): Boolean {
        val transactionDate =
            transaction.transaction.overview.blockTimeEpochSeconds
                ?.let { blockTimeEpochSeconds ->
                    Instant.ofEpochSecond(blockTimeEpochSeconds).atZone(ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()

        val hasMemo = transaction.transaction.overview.memoCount > 0
        val restoreDate = restoreTimestamp.atZone(ZoneId.systemDefault()).toLocalDate()

        return if (hasMemo && transactionDate < restoreDate) {
            false
        } else {
            val transactionMetadata = transaction.transactionMetadata

            hasMemo && (transactionMetadata == null || transactionMetadata.isRead.not())
        }
    }

    private fun isBookmark(transaction: FilterTransactionData): Boolean {
        return transaction.transactionMetadata?.isBookmarked ?: false
    }

    private fun hasNotes(transaction: FilterTransactionData): Boolean {
        return transaction.transactionMetadata?.note != null
    }

    private fun hasNotesWithFulltext(
        transaction: FilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        return transaction.transactionMetadata?.note
            ?.contains(
                fulltextFilter,
                ignoreCase = true
            )
            ?: false
    }

    private fun hasAmountWithFulltext(
        transaction: FilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        val text = stringRes(transaction.transaction.overview.netValue).getString(context)
        return text.contains(fulltextFilter, ignoreCase = true)
    }

    private fun hasAddressWithFulltext(
        transaction: FilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        return transaction.recipientAddress?.contains(fulltextFilter, ignoreCase = true) ?: false
    }

    private fun hasContactInAddressBookWithFulltext(
        transaction: FilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        return transaction.contact?.name?.contains(fulltextFilter, ignoreCase = true) ?: false
    }

    private fun hasMemoInFilteredIds(
        memoTxIds: List<TransactionId>?,
        transaction: FilterTransactionData
    ) = memoTxIds?.contains(transaction.transaction.overview.txId) ?: false
}

private data class FilterTransactionData(
    val transaction: TransactionData,
    val contact: AddressBookContact?,
    val recipientAddress: String?,
    val transactionMetadata: TransactionMetadata?
)

private const val MIN_TEXT_FILTER_LENGTH = 3
