package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.mapper.ActivityMapper
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.usecase.GetActivitiesUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToRequestShieldedUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.swap.detail.SwapDetailArgs
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailArgs
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionHistoryWidgetVM(
    getActivities: GetActivitiesUseCase,
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    private val activityMapper: ActivityMapper,
    private val navigationRouter: NavigationRouter,
    private val restoreTimestampDataSource: RestoreTimestampDataSource,
    private val navigateToRequestShielded: NavigateToRequestShieldedUseCase,
    // private val navigateToCoinbase: NavigateToCoinbaseUseCase,
    // private val getVersionInfoProvider: GetVersionInfoProvider,
) : ViewModel() {
    val state =
        combine(
            getActivities.observe(),
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
                                onClick = ::onRequestZecClick
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
                                    activityMapper.createTransactionState(
                                        data = transaction,
                                        restoreTimestamp = restoreTimestampDataSource.getOrCreate(),
                                        onTransactionClick = ::onTransactionClick,
                                        onSwapClick = ::onSwapClick
                                    )
                                }
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = TransactionHistoryWidgetState.Loading
        )

    private fun onSwapClick(depositAddress: String) = navigationRouter.forward(SwapDetailArgs(depositAddress))

    private fun onTransactionClick(transaction: Transaction) {
        navigationRouter.forward(TransactionDetailArgs(transaction.id.txIdString()))
    }

    private fun onSeeAllTransactionsClick() {
        navigationRouter.forward(TransactionHistory)
    }

    private fun onRequestZecClick() = viewModelScope.launch { navigateToRequestShielded() }
}

private const val MAX_TRANSACTION_COUNT = 5
