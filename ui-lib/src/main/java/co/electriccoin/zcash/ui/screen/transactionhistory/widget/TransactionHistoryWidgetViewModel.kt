package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.mapper.TransactionHistoryMapper
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.usecase.GetCurrentTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSendUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetail
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TransactionHistoryWidgetViewModel(
    getCurrentTransactions: GetCurrentTransactionsUseCase,
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val navigationRouter: NavigationRouter,
    private val navigateToSend: NavigateToSendUseCase,
    private val restoreTimestampDataSource: RestoreTimestampDataSource,
) : ViewModel() {
    val state =
        combine(
            getCurrentTransactions.observe(),
            getWalletRestoringState.observe(),
        ) { transactions, restoringState ->
            when {
                transactions == null -> TransactionHistoryWidgetState.Loading
                transactions.isEmpty() ->
                    TransactionHistoryWidgetState.Empty(
                        subtitle =
                            stringRes(R.string.transaction_history_widget_empty_subtitle)
                                .takeIf { restoringState != WalletRestoringState.RESTORING },
                        sendTransaction =
                            ButtonState(
                                text = stringRes(R.string.transaction_history_send_transaction),
                                onClick = ::onSendTransactionClick
                            ).takeIf { restoringState != WalletRestoringState.RESTORING },
                        enableShimmer = restoringState == WalletRestoringState.RESTORING
                    )

                else ->
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
                                        restoreTimestamp = restoreTimestampDataSource.getOrCreate(),
                                        onTransactionClick = ::onTransactionClick,
                                    )
                                }
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                TransactionHistoryWidgetState.Loading
        )

    private fun onTransactionClick(transactionData: TransactionData) {
        navigationRouter.forward(TransactionDetail(transactionData.overview.txIdString()))
    }

    private fun onSeeAllTransactionsClick() {
        navigationRouter.forward(TransactionHistory)
    }

    private fun onSendTransactionClick() {
        navigateToSend()
    }
}

private const val MAX_TRANSACTION_COUNT = 5
