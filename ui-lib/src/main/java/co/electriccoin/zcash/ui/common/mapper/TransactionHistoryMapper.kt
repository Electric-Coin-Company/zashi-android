package co.electriccoin.zcash.ui.common.mapper

import cash.z.ecc.android.sdk.model.TransactionPool
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.TransactionData
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVE_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SEND_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENT
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING_FAILED
import co.electriccoin.zcash.ui.common.usecase.ListTransactionData
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StringResourceColor
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class TransactionHistoryMapper {
    fun createTransactionState(
        transaction: ListTransactionData,
        restoreTimestamp: Instant,
        onTransactionClick: (TransactionData) -> Unit
    ): TransactionState {
        val transactionDate =
            transaction.data.overview.blockTimeEpochSeconds
                ?.let { blockTimeEpochSeconds ->
                    Instant.ofEpochSecond(blockTimeEpochSeconds).atZone(ZoneId.systemDefault())
                }

        return TransactionState(
            key = transaction.data.overview.txIdString(),
            icon = getIcon(transaction),
            title = getTitle(transaction),
            subtitle = getSubtitle(transactionDate),
            isShielded = isShielded(transaction),
            value = getValue(transaction),
            onClick = { onTransactionClick(transaction.data) },
            isUnread = isUnread(transaction, transactionDate, restoreTimestamp)
        )
    }

    private fun isUnread(
        transaction: ListTransactionData,
        transactionDateTime: ZonedDateTime?,
        restoreTimestamp: Instant,
    ): Boolean {
        val hasMemo = transaction.data.overview.memoCount > 0
        val transactionDate = transactionDateTime?.toLocalDate() ?: LocalDate.now()
        val restoreDate = restoreTimestamp.atZone(ZoneId.systemDefault()).toLocalDate()

        return if (hasMemo && transactionDate < restoreDate) {
            false
        } else {
            val transactionMetadata = transaction.metadata
            hasMemo && (transactionMetadata == null || transactionMetadata.isMemoRead.not())
        }
    }

    private fun getIcon(transaction: ListTransactionData) =
        when (transaction.data.state) {
            SENT  -> R.drawable.ic_transaction_sent
            SENDING  -> R.drawable.ic_transaction_send_pending
            SEND_FAILED -> R.drawable.ic_transaction_send_failed

            RECEIVED  -> R.drawable.ic_transaction_received
            RECEIVING  -> R.drawable.ic_transaction_receive_pending
            RECEIVE_FAILED -> R.drawable.ic_transaction_receive_pending

            SHIELDED  -> R.drawable.ic_transaction_shielded
            SHIELDING  -> R.drawable.ic_transaction_shield_pending
            SHIELDING_FAILED -> R.drawable.ic_transaction_shield_failed
        }

    private fun getTitle(transaction: ListTransactionData) =
        when (transaction.data.state) {
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

    private fun getSubtitle(transactionDate: ZonedDateTime?): StringResource? {
        if (transactionDate == null) return null
        val daysBetween = ChronoUnit.DAYS.between(transactionDate.toLocalDate(), LocalDate.now())
        return when {
            LocalDate.now() == transactionDate.toLocalDate() ->
                stringRes(R.string.transaction_history_today)

            LocalDate.now().minusDays(1) == transactionDate.toLocalDate() ->
                stringRes(R.string.transaction_history_yesterday)

            daysBetween < MONTH_THRESHOLD ->
                stringRes(R.string.transaction_history_days_ago, daysBetween.toString())

            else -> stringResByDateTime(zonedDateTime = transactionDate, useFullFormat = false)
        }
    }

    private fun isShielded(transaction: ListTransactionData) =
        transaction.data.transactionOutputs
            .none { output -> output.pool == TransactionPool.TRANSPARENT } &&
            !transaction.data.overview.isShielding

    private fun getValue(transaction: ListTransactionData) =
        when (transaction.data.state) {
            SENT,
            SENDING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(transaction.data.overview.netValue)
                    )
                )

            SEND_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(transaction.data.overview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )

            RECEIVED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.data.overview.netValue)
                    ),
                    StringResourceColor.POSITIVE
                )

            RECEIVING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.data.overview.netValue)
                    ),
                )

            RECEIVE_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.data.overview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )

            SHIELDED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.data.overview.netValue)
                    )
                )

            SHIELDING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.data.overview.netValue)
                    )
                )

            SHIELDING_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.data.overview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )
        }
}

private const val MONTH_THRESHOLD = 30
