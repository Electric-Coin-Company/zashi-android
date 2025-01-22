package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.TransactionPool
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.ObserveCurrentTransactionsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TransactionHistoryWidgetViewModel(
    observeCurrentTransactions: ObserveCurrentTransactionsUseCase,
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
                            TransactionState(
                                icon = when {
                                    transaction.transactionOverview.isShielding -> R.drawable.ic_transaction_shielded
                                    transaction.transactionOverview.isSentTransaction -> R.drawable.ic_transaction_sent
                                    else -> R.drawable.ic_transaction_received
                                },
                                title = when {
                                    transaction.transactionOverview.isShielding -> stringRes("Shielded")
                                    transaction.transactionOverview.isSentTransaction -> stringRes("Sent")
                                    else -> stringRes("Received")
                                },
                                subtitle = transaction.transactionOverview.blockTimeEpochSeconds
                                    ?.let { blockTimeEpochSeconds ->
                                        Instant.ofEpochSecond(blockTimeEpochSeconds).toStringResource()
                                    },
                                isShielded = transaction.transactionOutputs
                                    .none { output -> output.pool == TransactionPool.TRANSPARENT } &&
                                    !transaction.transactionOverview.isShielding,
                                value = when {
                                    transaction.transactionOverview.isShielding -> null
                                    transaction.transactionOverview.isSentTransaction -> stringRes(
                                        R.string.transaction_history_minus,
                                        stringRes(transaction.transactionOverview.netValue)
                                    )

                                    else -> stringRes(
                                        R.string.transaction_history_plus,
                                        stringRes(transaction.transactionOverview.netValue)
                                    )
                                },
                                onClick = ::onTransactionClick
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

    private fun Instant.toStringResource(): StringResource =
        when (val date = this.atZone(ZoneId.systemDefault()).toLocalDate()) {
            LocalDate.now() -> stringRes(R.string.transaction_history_today)
            LocalDate.now().minusDays(1) -> stringRes(R.string.transaction_history_yesterday)
            else -> stringRes(
                R.string.transaction_history_days_ago,
                ChronoUnit.DAYS.between(date, LocalDate.now()).toString()
            )
        }

    private fun onTransactionClick() {
        // todo
    }

    private fun onSeeAllTransactionsClick() {
        // todo
    }

    private fun onSendTransactionClick() {
        // todo
    }
}