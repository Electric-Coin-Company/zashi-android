package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
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

    val state = observeCurrentTransactions()
        .map {
            if (it.isNullOrEmpty()) {
                TransactionHistoryWidgetState.Empty(
                    sendTransaction = ButtonState(
                        text = stringRes("Send a transaction"),
                        onClick = ::onSendTransactionClick
                    )
                )
            } else {
                TransactionHistoryWidgetState.Data(
                    header = TransactionHistoryWidgetHeaderState(
                        title = stringRes("Transactions"),
                        button = ButtonState(
                            text = stringRes("See All"),
                            onClick = ::onSeeAllTransactionsClick
                        )
                    ),
                    transactions = it
                        .take(5)
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
            initialValue = TransactionHistoryWidgetState.Empty(
                sendTransaction = ButtonState(
                    text = stringRes("Send a transaction"),
                    onClick = ::onSendTransactionClick
                )
            )
        )

    private fun onTransactionClick(transactionData: TransactionData) {
        // todo
    }

    private fun onSeeAllTransactionsClick() {
        navigationRouter.forward(TransactionHistory)
    }

    private fun onSendTransactionClick() {
        // todo
    }
}