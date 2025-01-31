package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.util.Log
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.TransactionMetadata
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.stringRes
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Duration.Companion.seconds

@Suppress("TooManyFunctions")
class GetCurrentFilteredTransactionsUseCase(
    private val metadataRepository: MetadataRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionFilterRepository: TransactionFilterRepository,
    private val restoreTimestampDataSource: RestoreTimestampDataSource,
    private val synchronizerProvider: SynchronizerProvider,
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
                                metadataRepository.observeTransactionMetadataByTxId(transaction.overview.txIdString())
                                    .map {
                                        DetailedFilterTransactionData(
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
                                        txId = transaction.overview.txIdString(),
                                    )
                                ) { contact, transactionMetadata ->
                                    DetailedFilterTransactionData(
                                        transaction = transaction,
                                        contact = contact,
                                        recipientAddress = recipient,
                                        transactionMetadata = transactionMetadata
                                    )
                                }
                            }
                        }

                if (enhancedTransactions != null) {
                    combine(enhancedTransactions.map { it }) { it.toList() }
                } else {
                    flowOf(null)
                }
            }
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds, 5.seconds),
                replay = 1
            )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val transactionsFilteredByFulltext: Flow<List<DetailedFilterTransactionData>?> =
        transactionFilterRepository
            .fulltextFilter
            .debounce(.69.seconds)
            .distinctUntilChanged()
            .flatMapLatest { fulltextFilter ->
                if (fulltextFilter == null || fulltextFilter.length < MIN_TEXT_FILTER_LENGTH) {
                    detailedCurrentTransactions
                        .onStart { emit(null) }
                } else {
                    combine(
                        detailedCurrentTransactions,
                        observeTransactionsByMemo(fulltextFilter)
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
                    }.onStart { emit(null) }
                }
            }
            .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        transactionsFilteredByFulltext.flatMapLatest { transactions ->
            transactionFilterRepository.filters
                .flatMapLatest { filters ->

                    flow {
                        emit(null)
                        val result =
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
                                ?.map { transaction -> transaction.transaction }

                        if (result != null) {
                            emit(result)
                        } else {
                            emit(null)
                        }
                    }
                }
        }.distinctUntilChanged()
            .onEach {
                Log.d("KKTINA", it.toString())
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTransactionsByMemo(memo: String): Flow<List<FirstClassByteArray>?> =
        synchronizerProvider
            .synchronizer
            .filterNotNull()
            .flatMapLatest { synchronizer ->
                synchronizer.getTransactionsByMemoSubstring(memo).onEmpty { emit(listOf()) }
            }
            .distinctUntilChanged()

    private fun filterByGeneralFilters(
        filters: List<TransactionFilter>,
        transaction: DetailedFilterTransactionData,
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
        transaction: DetailedFilterTransactionData
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
        transaction: DetailedFilterTransactionData,
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

            hasMemo && (transactionMetadata == null || transactionMetadata.isMemoRead.not())
        }
    }

    private fun isBookmark(transaction: DetailedFilterTransactionData): Boolean {
        return transaction.transactionMetadata?.isBookmark ?: false
    }

    private fun hasNotes(transaction: DetailedFilterTransactionData) = transaction.transactionMetadata != null

    private fun hasNotesWithFulltext(
        transaction: DetailedFilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        return transaction.transactionMetadata?.notes?.any { it.content.contains(fulltextFilter, ignoreCase = true) }
            ?: false
    }

    private fun hasAmountWithFulltext(
        transaction: DetailedFilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        val text = stringRes(transaction.transaction.overview.netValue).getString(context)
        return text.contains(fulltextFilter, ignoreCase = true)
    }

    private fun hasAddressWithFulltext(
        transaction: DetailedFilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        return transaction.recipientAddress?.contains(fulltextFilter, ignoreCase = true) ?: false
    }

    private fun hasContactInAddressBookWithFulltext(
        transaction: DetailedFilterTransactionData,
        fulltextFilter: String
    ): Boolean {
        return transaction.contact?.name?.contains(fulltextFilter, ignoreCase = true) ?: false
    }

    private fun hasMemoInFilteredIds(
        memoTxIds: List<FirstClassByteArray>?,
        transaction: DetailedFilterTransactionData
    ) = memoTxIds?.contains(transaction.transaction.overview.rawId) ?: false
}

data class DetailedFilterTransactionData(
    val transaction: TransactionData,
    val contact: AddressBookContact?,
    val recipientAddress: String?,
    val transactionMetadata: TransactionMetadata?
)

private const val MIN_TEXT_FILTER_LENGTH = 3
