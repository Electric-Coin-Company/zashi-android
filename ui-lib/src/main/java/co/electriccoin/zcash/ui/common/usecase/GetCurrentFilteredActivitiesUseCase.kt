package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import cash.z.ecc.android.sdk.model.TransactionId
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.SendTransaction
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.combineToFlow
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.util.CloseableScopeHolder
import co.electriccoin.zcash.ui.util.CloseableScopeHolderImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
class GetCurrentFilteredActivitiesUseCase(
    getActivities: GetActivitiesUseCase,
    transactionFilterRepository: TransactionFilterRepository,
    private val transactionRepository: TransactionRepository,
    private val restoreTimestampDataSource: RestoreTimestampDataSource,
    private val addressBookRepository: AddressBookRepository,
    private val context: Context,
) : CloseableScopeHolder by CloseableScopeHolderImpl(coroutineContext = Dispatchers.IO) {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val detailedCurrentTransactions =
        getActivities.observe()
            .flatMapLatest { activities ->
                val enhancedTransactions =
                    activities
                        ?.map { activity ->
                            when (activity) {
                                is ActivityData.BySwap -> flowOf(FilterActivityData.BySwap(activity))
                                is ActivityData.ByTransaction -> {
                                    val recipient = activity.transaction.recipient

                                    if (recipient == null) {
                                        flowOf(
                                            FilterActivityData.ByTransaction(
                                                activity = activity,
                                                contact = null,
                                            )
                                        )
                                    } else {
                                        addressBookRepository.observeContactByAddress(recipient.address)
                                            .map { contact ->
                                                FilterActivityData.ByTransaction(
                                                    activity = activity,
                                                    contact = contact,
                                                )
                                            }
                                    }
                                }
                            }
                        }

                enhancedTransactions?.combineToFlow() ?: flowOf(null)
            }.shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5.seconds, 5.seconds),
                replay = 1
            )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val transactionsFilteredByFulltext: Flow<List<FilterActivityData>?> =
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
                            }.mapLatest { (activities, memoTxIds) ->
                                activities
                                    ?.filter { activity ->
                                        hasMemoInFilteredIds(memoTxIds, activity) ||
                                            hasContactInAddressBookWithFulltext(activity, fulltextFilter) ||
                                            hasAddressWithFulltext(activity, fulltextFilter) ||
                                            hasNotesWithFulltext(activity, fulltextFilter) ||
                                            hasAmountWithFulltext(activity, fulltextFilter)
                                    }
                            }
                        }
                    )
                }
            }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val result =
        transactionFilterRepository.filters
            .flatMapLatest { filters ->
                flow {
                    emit(null)
                    emitAll(
                        transactionsFilteredByFulltext
                            .mapLatest { transactions ->
                                transactions
                                    ?.filter { transaction ->
                                        filterBySentReceivedSwap(filters, transaction)
                                    }?.filter { transaction ->
                                        filterByGeneralFilters(
                                            filters = filters,
                                            activity = transaction,
                                            restoreTimestamp = restoreTimestampDataSource.getOrCreate()
                                        )
                                    }?.map { transaction ->
                                        transaction.activity
                                    }
                            }
                    )
                }
            }.distinctUntilChanged()

    fun observe(): Flow<List<ActivityData>?> = result

    private fun filterByGeneralFilters(
        filters: List<TransactionFilter>,
        activity: FilterActivityData,
        restoreTimestamp: Instant,
    ): Boolean {
        val memoPass =
            if (filters.contains(TransactionFilter.MEMOS)) {
                when (activity) {
                    is FilterActivityData.BySwap -> false
                    is FilterActivityData.ByTransaction -> activity.activity.transaction.memoCount > 0
                }
            } else {
                true
            }
        val unreadPass = if (filters.contains(TransactionFilter.UNREAD)) isUnread(activity, restoreTimestamp) else true
        val bookmarkPass = if (filters.contains(TransactionFilter.BOOKMARKED)) isBookmark(activity) else true
        val notesPass = if (filters.contains(TransactionFilter.NOTES)) hasNotes(activity) else true
        return memoPass && unreadPass && bookmarkPass && notesPass
    }

    @Suppress
    private fun filterBySentReceivedSwap(
        filters: List<TransactionFilter>,
        activity: FilterActivityData
    ): Boolean =
        if (TransactionFilter.SENT in filters ||
            TransactionFilter.RECEIVED in filters ||
            TransactionFilter.SWAP in filters
        ) {
            when {
                TransactionFilter.SENT in filters && when (activity) {
                    is FilterActivityData.BySwap -> false
                    is FilterActivityData.ByTransaction -> activity.activity.transaction is SendTransaction
                } -> true

                TransactionFilter.RECEIVED in filters && when (activity) {
                    is FilterActivityData.BySwap -> false
                    is FilterActivityData.ByTransaction -> activity.activity.transaction is ReceiveTransaction
                } -> true

                TransactionFilter.SWAP in filters && when (activity) {
                    is FilterActivityData.BySwap -> true
                    is FilterActivityData.ByTransaction -> activity.activity.metadata.swapMetadata != null
                } -> true

                else -> false
            }
        } else {
            true
        }

    private fun isUnread(activity: FilterActivityData, restoreTimestamp: Instant): Boolean {
        if (activity !is FilterActivityData.ByTransaction) return false

        val transactionDate =
            activity.activity.transaction.timestamp
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate()
                ?: LocalDate.now()

        val hasMemo = activity.activity.transaction.memoCount > 0
        val restoreDate = restoreTimestamp.atZone(ZoneId.systemDefault()).toLocalDate()

        return if (hasMemo && transactionDate < restoreDate) {
            false
        } else {
            val transactionMetadata = activity.activity.metadata

            hasMemo && transactionMetadata.isRead.not()
        }
    }

    private fun isBookmark(activity: FilterActivityData): Boolean {
        return activity is FilterActivityData.ByTransaction && activity.activity.metadata.isBookmarked
    }

    private fun hasNotes(activity: FilterActivityData): Boolean {
        return activity is FilterActivityData.ByTransaction && activity.activity.metadata.note != null
    }

    private fun hasNotesWithFulltext(
        activity: FilterActivityData,
        fulltextFilter: String
    ): Boolean {
        if (activity !is FilterActivityData.ByTransaction) return false

        return activity.activity.metadata.note
            ?.contains(
                fulltextFilter,
                ignoreCase = true
            )
            ?: false
    }

    private fun hasAmountWithFulltext(activity: FilterActivityData, fulltextFilter: String): Boolean {
        return when (activity) {
            is FilterActivityData.BySwap -> {
                val text = stringResByNumber(activity.activity.swap.totalFeesUsd).getString(context)
                text.contains(fulltextFilter.trim(), ignoreCase = true)
            }

            is FilterActivityData.ByTransaction -> {
                val text = stringRes(activity.activity.transaction.amount, HIDDEN).getString(context)
                text.contains(fulltextFilter.trim(), ignoreCase = true)
            }
        }
    }

    private fun hasAddressWithFulltext(transaction: FilterActivityData, fulltextFilter: String): Boolean {
        return when (transaction) {
            is FilterActivityData.BySwap -> transaction.activity.swap.depositAddress
                .contains(fulltextFilter, true)

            is FilterActivityData.ByTransaction -> {
                transaction.activity.transaction.recipient?.address
                    ?.contains(fulltextFilter, ignoreCase = true) ?: false
            }
        }
    }

    private fun hasContactInAddressBookWithFulltext(activity: FilterActivityData, fulltextFilter: String): Boolean {
        if (activity !is FilterActivityData.ByTransaction) return false
        return activity.contact?.name?.contains(fulltextFilter, ignoreCase = true) ?: false
    }

    private fun hasMemoInFilteredIds(memoTxIds: List<TransactionId>?, activity: FilterActivityData): Boolean {
        if (activity !is FilterActivityData.ByTransaction) return false

        return memoTxIds?.contains(activity.activity.transaction.id) ?: false
    }
}

private sealed interface FilterActivityData {

    val activity: ActivityData

    data class ByTransaction(
        override val activity: ActivityData.ByTransaction,
        val contact: EnhancedABContact?,
    ) : FilterActivityData

    data class BySwap(override val activity: ActivityData.BySwap) : FilterActivityData
}

private const val MIN_TEXT_FILTER_LENGTH = 3
