package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.usecase.GetCurrentFilteredTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionfilters.TransactionFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

class TransactionHistoryViewModel(
    getCurrentTransactions: GetCurrentFilteredTransactionsUseCase,
    getTransactionFilters: GetTransactionFiltersUseCase,
    transactionFilterRepository: TransactionFilterRepository,
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val navigationRouter: NavigationRouter,
    private val resetTransactionFilters: ResetTransactionFiltersUseCase,
) : ViewModel() {
    val onScrollToTopRequested = transactionFilterRepository.onFilterChanged

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("SpreadOperator")
    val state =
        combine(getCurrentTransactions.observe(), getTransactionFilters.observe()) { transactions, filters ->
            transactions to filters
        }.mapLatest { (transactions, filters) ->
            val items =
                transactions.orEmpty()
                    .groupBy {
                        val now = ZonedDateTime.now().toLocalDate()
                        val other =
                            Instant
                                .ofEpochSecond(it.transactionOverview.blockTimeEpochSeconds ?: 0)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

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
                                            onTransactionClick = ::onTransactionClick
                                        )
                                )
                            }.toTypedArray()
                        )
                    }
                    .flatten()

            createState(items = items, filtersSize = filters.size)
        }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(items = emptyList(), filtersSize = 0)
            )

    override fun onCleared() {
        resetTransactionFilters()
        super.onCleared()
    }

    private fun createState(
        items: List<TransactionHistoryItem>,
        filtersSize: Int
    ) = TransactionHistoryState(
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

    private fun onBack() {
        navigationRouter.back()
    }

    @Suppress("EmptyFunctionBlock")
    private fun onTransactionClick(transactionData: TransactionData) {
        Twig.debug { "Clicked txid: ${transactionData.transactionOverview.txIdString()}" }
    }

    private fun onTransactionFiltersClicked() = navigationRouter.forward(TransactionFilters)
}

private const val WEEK_THRESHOLD = 7L
private const val MONTH_THRESHOLD = 30L
