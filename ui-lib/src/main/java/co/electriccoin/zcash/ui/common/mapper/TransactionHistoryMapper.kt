package co.electriccoin.zcash.ui.common.mapper

import cash.z.ecc.android.sdk.model.TransactionPool
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TransactionHistoryMapper {
    fun createTransactionState(
        transaction: TransactionData,
        onTransactionClick: (TransactionData) -> Unit
    ) = TransactionState(
        key = transaction.transactionOverview.txIdString(),
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
        onClick = { onTransactionClick(transaction) }
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
}