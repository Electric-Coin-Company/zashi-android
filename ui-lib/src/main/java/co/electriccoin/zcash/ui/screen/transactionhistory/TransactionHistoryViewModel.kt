package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.usecase.ApplyTransactionFulltextFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.GetCurrentFilteredTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.ListTransactionData
import co.electriccoin.zcash.ui.common.usecase.ResetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailArgs
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFiltersArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

class TransactionHistoryViewModel(
    getCurrentFilteredTransactions: GetCurrentFilteredTransactionsUseCase,
    getTransactionFilters: GetTransactionFiltersUseCase,
    transactionFilterRepository: TransactionFilterRepository,
    private val applyTransactionFulltextFilters: ApplyTransactionFulltextFiltersUseCase,
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val navigationRouter: NavigationRouter,
    private val resetTransactionFilters: ResetTransactionFiltersUseCase,
    private val restoreTimestampDataSource: RestoreTimestampDataSource
) : ViewModel() {
    val search =
        transactionFilterRepository.fulltextFilter
            .map {
                TextFieldState(stringRes(it.orEmpty()), onValueChange = ::onFulltextFilterChanged)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue =
                    TextFieldState(
                        stringRes(
                            transactionFilterRepository.fulltextFilter.value.orEmpty()
                        ),
                        onValueChange = ::onFulltextFilterChanged
                    )
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        combine(
            getCurrentFilteredTransactions.observe(),
            getTransactionFilters.observe(),
        ) { transactions, filters ->
            transactions to filters
        }.mapLatest { (transactions, filters) ->
            when {
                transactions == null ->
                    createLoadingState(
                        filtersSize = filters.size,
                    )

                transactions.isEmpty() ->
                    createEmptyState(
                        filtersSize = filters.size,
                    )

                else ->
                    createDataState(
                        transactions = transactions,
                        filtersSize = filters.size,
                        restoreTimestamp = restoreTimestampDataSource.getOrCreate()
                    )
            }
        }.flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue =
                    createLoadingState(
                        filtersSize = 0,
                    )
            )

    override fun onCleared() {
        resetTransactionFilters()
        super.onCleared()
    }

    private fun createDataState(
        transactions: List<ListTransactionData>,
        filtersSize: Int,
        restoreTimestamp: Instant,
    ): TransactionHistoryState.Data {
        val now = ZonedDateTime.now().toLocalDate()

        val items =
            transactions
                .groupBy {
                    val other =
                        it.transaction.timestamp
                            ?.atZone(ZoneId.systemDefault())
                            ?.toLocalDate() ?: now
                    when {
                        now == other ->
                            stringRes(R.string.transaction_history_today) to "today"

                        other == now.minusDays(1) ->
                            stringRes(R.string.transaction_history_yesterday) to "yesterday"

                        other >= now.minusDays(WEEK_THRESHOLD) ->
                            stringRes(R.string.transaction_history_previous_7_days) to "previous_7_days"

                        other >= now.minusDays(MONTH_THRESHOLD) ->
                            stringRes(R.string.transaction_history_previous_30_days) to "previous_30_days"

                        else -> {
                            val yearMonth = YearMonth.from(other)
                            stringRes(yearMonth) to yearMonth.toString()
                        }
                    }
                }.map { (entry, transactions) ->
                    val (headerStringRes, headerId) = entry
                    listOf(
                        TransactionHistoryItem.Header(
                            title = headerStringRes,
                            key = headerId,
                        ),
                    ) +
                        transactions.map { transaction ->
                            TransactionHistoryItem.Transaction(
                                state =
                                    transactionHistoryMapper.createTransactionState(
                                        data = transaction,
                                        onTransactionClick = ::onTransactionClick,
                                        restoreTimestamp = restoreTimestamp
                                    )
                            )
                        }
                }.flatten()

        return TransactionHistoryState.Data(
            onBack = ::onBack,
            items = items,
            filterButton =
                IconButtonState(
                    icon =
                        if (filtersSize <= 0) {
                            R.drawable.ic_transaction_filters
                        } else {
                            R.drawable.ic_transactions_filters_selected
                        },
                    badge = stringRes(filtersSize.toString()).takeIf { filtersSize > 0 },
                    onClick = ::onTransactionFiltersClicked,
                    contentDescription = null
                ),
        )
    }

    private fun onFulltextFilterChanged(value: String) {
        applyTransactionFulltextFilters(value)
    }

    private fun createLoadingState(filtersSize: Int) =
        TransactionHistoryState.Loading(
            onBack = ::onBack,
            filterButton =
                IconButtonState(
                    icon =
                        if (filtersSize <= 0) {
                            R.drawable.ic_transaction_filters
                        } else {
                            R.drawable.ic_transactions_filters_selected
                        },
                    badge = stringRes(filtersSize.toString()).takeIf { filtersSize > 0 },
                    onClick = ::onTransactionFiltersClicked,
                    contentDescription = null
                ),
        )

    private fun createEmptyState(filtersSize: Int) =
        TransactionHistoryState.Empty(
            onBack = ::onBack,
            filterButton =
                IconButtonState(
                    icon =
                        if (filtersSize <= 0) {
                            R.drawable.ic_transaction_filters
                        } else {
                            R.drawable.ic_transactions_filters_selected
                        },
                    badge = stringRes(filtersSize.toString()).takeIf { filtersSize > 0 },
                    onClick = ::onTransactionFiltersClicked,
                    contentDescription = null
                ),
        )

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onTransactionClick(transaction: Transaction) {
        navigationRouter.forward(TransactionDetailArgs(transaction.id.txIdString()))
    }

    private fun onTransactionFiltersClicked() = navigationRouter.forward(TransactionFiltersArgs)
}

private const val WEEK_THRESHOLD = 7L
private const val MONTH_THRESHOLD = 30L
