package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.usecase.ObserveCurrentTransactionsUseCase
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class TransactionHistoryViewModel(
    observeCurrentTransactions: ObserveCurrentTransactionsUseCase,
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {

    @Suppress("SpreadOperator")
    val state =
        observeCurrentTransactions()
            .map { transactions ->
                val items =
                    transactions.orEmpty()
                        .groupBy {
                            val now = ZonedDateTime.now()
                            val other =
                                Instant
                                    .ofEpochSecond(it.transactionOverview.blockTimeEpochSeconds ?: 0)
                                    .atZone(ZoneId.systemDefault())
                            ChronoUnit.WEEKS.between(other, now)
                        }
                        .map { (weekDifference, transactions) ->
                            listOf(
                                TransactionHistoryItem.Header(
                                    key = weekDifference,
                                    title =
                                        when (weekDifference) {
                                            0L -> stringRes(R.string.transaction_history_this_week)
                                            1L -> stringRes(R.string.transaction_history_last_week)
                                            else ->
                                                stringRes(
                                                    R.string.transaction_history_weeks_ago,
                                                    weekDifference.toString()
                                                )
                                        }
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

                createState(items = items)
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(items = emptyList())
            )

    private fun createState(items: List<TransactionHistoryItem>) =
        TransactionHistoryState(
            onBack = ::onBack,
            items = items,
            filterButton =
                IconButtonState(
                    icon = R.drawable.ic_transaction_filters,
                    onClick = {},
                    contentDescription = null
                ),
            search = TextFieldState(stringRes("")) {}
        )

    private fun onBack() {
        navigationRouter.back()
    }

    @Suppress("EmptyFunctionBlock", "UnusedParameter")
    private fun onTransactionClick(transactionData: TransactionData) {
    }
}
