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
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StringResourceColor
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
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
        key = transaction.overview.txIdString(),
        icon = getIcon(transaction),
        title = getTitle(transaction),
        subtitle = getSubtitle(transaction),
        isShielded = isShielded(transaction),
        value = getValue(transaction),
        onClick = { onTransactionClick(transaction) },
        hasMemo = transaction.overview.memoCount > 0
    )

    private fun getIcon(transaction: TransactionData) =
        when (transaction.state) {
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
        when (transaction.state) {
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
        val transactionDate =
            transaction.overview.blockTimeEpochSeconds
                ?.let { blockTimeEpochSeconds ->
                    Instant.ofEpochSecond(blockTimeEpochSeconds)
                }
                ?.atZone(ZoneId.systemDefault()) ?: return null
        val daysBetween = ChronoUnit.DAYS.between(transactionDate.toLocalDate(), LocalDate.now())
        return when {
            LocalDate.now() == transactionDate.toLocalDate() -> {
                stringRes(R.string.transaction_history_today)
            }

            LocalDate.now().minusDays(1) == transactionDate.toLocalDate() -> {
                stringRes(R.string.transaction_history_yesterday)
            }

            daysBetween < MONTH_THRESHOLD -> {
                stringRes(R.string.transaction_history_days_ago, daysBetween.toString())
            }

            else -> {
                stringResByDateTime(zonedDateTime = transactionDate, useFullFormat = false)
            }
        }
    }

    private fun isShielded(transaction: TransactionData) =
        transaction.transactionOutputs
            .none { output -> output.pool == TransactionPool.TRANSPARENT } &&
            !transaction.overview.isShielding

    private fun getValue(transaction: TransactionData) =
        when (transaction.state) {
            SENT,
            SENDING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(transaction.overview.netValue)
                    )
                )

            SEND_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(transaction.overview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )

            RECEIVED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.overview.netValue)
                    ),
                    StringResourceColor.POSITIVE
                )

            RECEIVING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.overview.netValue)
                    ),
                )

            RECEIVE_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.overview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )

            SHIELDED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.overview.netValue)
                    )
                )

            SHIELDING ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.overview.netValue)
                    )
                )

            SHIELDING_FAILED ->
                StyledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(transaction.overview.netValue)
                    ),
                    StringResourceColor.NEGATIVE
                )
        }
}

private const val MONTH_THRESHOLD = 30
