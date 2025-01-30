package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.common.model.Metadata
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.usecase.GetCurrentFilteredTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetMetadataUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetail
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.withIndex
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

class TransactionHistoryViewModel(
    getCurrentFilteredTransactions: GetCurrentFilteredTransactionsUseCase,
    getTransactionFilters: GetTransactionFiltersUseCase,
    transactionFilterRepository: TransactionFilterRepository,
    getMetadata: GetMetadataUseCase,
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val navigationRouter: NavigationRouter,
    private val resetTransactionFilters: ResetTransactionFiltersUseCase,
) : ViewModel() {
    val onScrollToTopRequested = transactionFilterRepository.onFilterChanged

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        combine(
            getCurrentFilteredTransactions.observe(),
            getTransactionFilters.observe(),
            getMetadata.observe()
        ) { transactions, filters, metadata ->
            Triple(transactions, filters, metadata)
        }.withIndex()
            .map {
                if (it.index == 0) {
                    delay(0.35.seconds)
                }
                it.value
            }
            .mapLatest { (transactions, filters, metadata) ->
                when {
                    transactions == null -> createLoadingState(filtersSize = filters.size)
                    transactions.isEmpty() -> createEmptyState(filtersSize = filters.size)
                    else -> createDataState(transactions, metadata, filters.size)
                }
            }.flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createLoadingState(filtersSize = 0)
            )

    override fun onCleared() {
        resetTransactionFilters()
        super.onCleared()
    }

    @Suppress("SpreadOperator")
    private fun createDataState(
        transactions: List<TransactionData>,
        metadata: Metadata,
        filtersSize: Int
    ): TransactionHistoryState.Data {
        val items =
            transactions
                .groupBy {
                    val now = ZonedDateTime.now().toLocalDate()
                    val other =
                        it.overview.blockTimeEpochSeconds?.let { sec ->
                            Instant
                                .ofEpochSecond(sec)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        } ?: LocalDate.now()
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
                }
                .map { (entry, transactions) ->
                    val (headerStringRes, headerId) = entry
                    listOf(
                        TransactionHistoryItem.Header(
                            title = headerStringRes,
                            key = headerId,
                        ),
                        *transactions.map { transaction ->
                            TransactionHistoryItem.Transaction(
                                state =
                                    transactionHistoryMapper.createTransactionState(
                                        transaction = transaction,
                                        metadata = metadata,
                                        onTransactionClick = ::onTransactionClick
                                    )
                            )
                        }.toTypedArray()
                    )
                }
                .flatten()

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
            search = TextFieldState(stringRes("")) {}
        )
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
            search = TextFieldState(stringRes("")) {}
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
            search = TextFieldState(stringRes("")) {}
        )

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onTransactionClick(transactionData: TransactionData) {
        navigationRouter.forward(TransactionDetail(transactionData.overview.txIdString()))
    }

    private fun onTransactionFiltersClicked() = navigationRouter.forward(TransactionFilters)
}

private const val WEEK_THRESHOLD = 7L
private const val MONTH_THRESHOLD = 30L
