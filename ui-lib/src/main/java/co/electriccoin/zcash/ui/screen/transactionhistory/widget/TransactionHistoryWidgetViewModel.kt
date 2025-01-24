package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.usecase.ObserveCurrentTransactionsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TransactionHistoryWidgetViewModel(
    observeCurrentTransactions: ObserveCurrentTransactionsUseCase,
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state =
        observeCurrentTransactions()
            .map { transactions ->
                if (transactions.isNullOrEmpty()) {
                    TransactionHistoryWidgetState.Empty(
                        sendTransaction =
                            ButtonState(
                                text = stringRes(R.string.transaction_history_send_transaction),
                                onClick = ::onSendTransactionClick
                            )
                    )
                } else {
                    TransactionHistoryWidgetState.Data(
                        header =
                            TransactionHistoryWidgetHeaderState(
                                title = stringRes(R.string.transaction_history_widget_title),
                                button =
                                    ButtonState(
                                        text = stringRes(R.string.transaction_history_widget_header_button),
                                        onClick = ::onSeeAllTransactionsClick
                                    ).takeIf {
                                        transactions.size > MAX_TRANSACTION_COUNT
                                    }
                            ),
                        transactions =
                            transactions
                                .take(MAX_TRANSACTION_COUNT)
                                .map { transaction ->
                                    transactionHistoryMapper.createTransactionState(
                                        transaction = transaction,
                                        onTransactionClick = ::onTransactionClick
                                    )
                                }
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue =
                    TransactionHistoryWidgetState.Empty(
                        sendTransaction =
                            ButtonState(
                                text = stringRes(R.string.transaction_history_widget_send_transaction),
                                onClick = ::onSendTransactionClick
                            )
                    )
            )

    @Suppress("EmptyFunctionBlock", "UnusedParameter")
    private fun onTransactionClick(transactionData: TransactionData) {
        Twig.debug { "Clicked txid: ${transactionData.transactionOverview.txIdString()}" }
    }

    private fun onSeeAllTransactionsClick() {
        navigationRouter.forward(TransactionHistory)
    }

    @Suppress("EmptyFunctionBlock")
    private fun onSendTransactionClick() {
    }
}

private const val MAX_TRANSACTION_COUNT = 5
