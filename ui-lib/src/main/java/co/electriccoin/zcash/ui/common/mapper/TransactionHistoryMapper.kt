package co.electriccoin.zcash.ui.common.mapper

import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionPool
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.RECEIVED
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.RECEIVE_FAILED
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.RECEIVING
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.SENDING
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.SEND_FAILED
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.SENT
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.SHIELDED
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.SHIELDING
import co.electriccoin.zcash.ui.common.mapper.TransactionExtendedState.SHIELDING_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StringResourceColor
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class TransactionHistoryMapper {
    fun createTransactionState(
        transaction: TransactionData,
        onTransactionClick: (TransactionData) -> Unit
    ) = TransactionState(
        key = transaction.transactionOverview.txIdString(),
        icon = getIcon(transaction),
        title = getTitle(transaction),
        subtitle = getSubtitle(transaction),
        isShielded = isShielded(transaction),
        value = getValue(transaction),
        onClick = { onTransactionClick(transaction) },
        hasMemo = transaction.transactionOverview.memoCount > 0
    )

    private fun getIcon(transaction: TransactionData) =
        when (transaction.transactionOverview.getExtendedState()) {
            SENT,
            SENDING,
            SEND_FAILED -> R.drawable.ic_transaction_sent

            RECEIVED,
            RECEIVING,
            RECEIVE_FAILED -> R.drawable.ic_transaction_received

            SHIELDED,
            SHIELDING,
            SHIELDING_FAILED -> R.drawable.ic_transaction_shielded
        }

    private fun getTitle(transaction: TransactionData) =
        when (transaction.transactionOverview.getExtendedState()) {
            SENT -> stringRes(R.string.transaction_history_sent)
            SENDING -> stringRes(R.string.transaction_history_sending)
            SEND_FAILED -> stringRes(R.string.transaction_history_sending_failed)
            RECEIVED -> stringRes(R.string.transaction_history_received)
            RECEIVING -> stringRes(R.string.transaction_history_receiving)
            RECEIVE_FAILED -> stringRes(R.string.transaction_history_receiving_failed)
            SHIELDED -> stringRes(R.string.transaction_history_shielded)
            SHIELDING -> stringRes(R.string.transaction_history_shielding)
            SHIELDING_FAILED -> stringRes(R.string.transaction_history_shielding_failed)
        }

    private fun getSubtitle(transaction: TransactionData): StringResource? {
        val now = ZonedDateTime.now()
        val transactionDate =
            transaction.transactionOverview.blockTimeEpochSeconds
                ?.let { blockTimeEpochSeconds ->
                    Instant.ofEpochSecond(blockTimeEpochSeconds)
                }
                ?.atZone(ZoneId.systemDefault()) ?: return null
        val daysBetween = ChronoUnit.DAYS.between(transactionDate, now)
        return if (daysBetween < MONTH_THRESHOLD) {
            stringRes(R.string.transaction_history_days_ago, daysBetween.toString())
        } else {
            stringRes(transactionDate)
        }
    }

    private fun isShielded(transaction: TransactionData) =
        transaction.transactionOutputs
            .none { output -> output.pool == TransactionPool.TRANSPARENT } &&
            !transaction.transactionOverview.isShielding

    private fun getValue(transaction: TransactionData) =
        when (transaction.transactionOverview.getExtendedState()) {
            SENT,
            SENDING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(transaction.transactionOverview.netValue)
                    )
                )
            SEND_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(transaction.transactionOverview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )
            RECEIVED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.transactionOverview.netValue)
                    ),
                    StringResourceColor.POSITIVE
                )
            RECEIVING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.transactionOverview.netValue)
                    ),
                )
            RECEIVE_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.transactionOverview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )
            SHIELDED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.transactionOverview.netValue)
                    ),
                    StringResourceColor.POSITIVE
                )
            SHIELDING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.transactionOverview.netValue)
                    )
                )
            SHIELDING_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.transactionOverview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )
        }
}

private fun TransactionOverview.getExtendedState(): TransactionExtendedState {
    return when (transactionState) {
        cash.z.ecc.android.sdk.model.TransactionState.Expired ->
            when {
                isShielding -> SHIELDING_FAILED
                isSentTransaction -> SEND_FAILED
                else -> RECEIVE_FAILED
            }

        cash.z.ecc.android.sdk.model.TransactionState.Confirmed ->
            when {
                isShielding -> SHIELDED
                isSentTransaction -> SENT
                else -> RECEIVED
            }

        cash.z.ecc.android.sdk.model.TransactionState.Pending ->
            when {
                isShielding -> SHIELDING
                isSentTransaction -> SENDING
                else -> RECEIVING
            }

        else -> error("Unexpected transaction state found while calculating its extended state.")
    }
}

private enum class TransactionExtendedState {
    SENT,
    SENDING,
    SEND_FAILED,
    RECEIVED,
    RECEIVING,
    RECEIVE_FAILED,
    SHIELDED,
    SHIELDING,
    SHIELDING_FAILED
}

private const val MONTH_THRESHOLD = 30
