package co.electriccoin.zcash.ui.common.mapper

import cash.z.ecc.android.sdk.model.TransactionPool
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.SendTransaction
import co.electriccoin.zcash.ui.common.repository.ShieldTransaction
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.usecase.ActivityData
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StringResourceColor
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.styledStringResource
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TransactionHistoryMapper {
    fun createTransactionState(
        data: ActivityData,
        restoreTimestamp: Instant,
        onTransactionClick: (Transaction) -> Unit,
        onSwapClick: (depositAddress: String) -> Unit
    ): TransactionState = when (data) {
        is ActivityData.BySwap -> TransactionState(
            key = data.swap.depositAddress,
            bigIcon = R.drawable.ic_activity_swap,
            smallIcon = R.drawable.ic_transaction_provider_near,
            title = stringRes(R.string.transaction_history_swapped),
            subtitle = getSubtitle(data.swap.lastUpdated),
            isShielded = false,
            value = styledStringResource(
                stringResByCurrencyNumber(data.swap.totalFeesUsd, "ZEC"),
                StringResourceColor.PRIMARY
            ),
            onClick = { onSwapClick(data.swap.depositAddress) },
            isUnread = false
        )
        is ActivityData.ByTransaction -> TransactionState(
            key = data.transaction.id.txIdString(),
            bigIcon = getBigIcon(data),
            smallIcon = getSmallIcon(data),
            title = getTitle(data),
            subtitle = getSubtitle(data.transaction.timestamp),
            isShielded = isShielded(data),
            value = getValue(data),
            onClick = { onTransactionClick(data.transaction) },
            isUnread = isUnread(data, restoreTimestamp)
        )
    }

    private fun isUnread(data: ActivityData.ByTransaction, restoreTimestamp: Instant, ): Boolean {
        val transactionDateTime = data.transaction.timestamp?.atZone(ZoneId.systemDefault())
        val hasMemo = data.transaction.memoCount > 0
        val transactionDate = transactionDateTime?.toLocalDate() ?: LocalDate.now()
        val restoreDate = restoreTimestamp.atZone(ZoneId.systemDefault()).toLocalDate()

        return if (hasMemo && transactionDate < restoreDate) {
            false
        } else {
            val transactionMetadata = data.metadata
            hasMemo && transactionMetadata.isRead.not()
        }
    }

    private fun getBigIcon(data: ActivityData.ByTransaction) =
        when (val transaction = data.transaction) {
            is ReceiveTransaction.Success -> R.drawable.ic_transaction_received
            is ReceiveTransaction.Pending -> R.drawable.ic_transaction_receive_pending
            is ReceiveTransaction.Failed -> R.drawable.ic_transaction_receive_pending
            is ShieldTransaction.Success -> R.drawable.ic_transaction_shielded
            is ShieldTransaction.Pending -> R.drawable.ic_transaction_shield_pending
            is ShieldTransaction.Failed -> R.drawable.ic_transaction_shield_failed
            is SendTransaction -> {
                val swapMetadata = data.metadata.swapMetadata
                if (swapMetadata != null && swapMetadata.provider.token == "zec") {
                    R.drawable.ic_activity_swap
                } else if (swapMetadata != null) {
                    R.drawable.ic_activity_crosspay
                } else {
                    when (transaction) {
                        is SendTransaction.Success -> R.drawable.ic_transaction_sent
                        is SendTransaction.Pending -> R.drawable.ic_transaction_send_pending
                        is SendTransaction.Failed -> R.drawable.ic_transaction_send_failed
                    }
                }
            }
        }

    private fun getSmallIcon(data: ActivityData.ByTransaction): Int? = if (data.metadata.swapMetadata
            ?.provider
            ?.provider
            ?.startsWith("near") == true
    ) {
        R.drawable.ic_transaction_provider_near
    } else {
        null
    }

    private fun getTitle(data: ActivityData.ByTransaction) =
        when (val transaction = data.transaction) {
            is ReceiveTransaction.Success -> stringRes(R.string.transaction_history_received)
            is ReceiveTransaction.Pending -> stringRes(R.string.transaction_history_receiving)
            is ReceiveTransaction.Failed -> stringRes(R.string.transaction_history_receiving_failed)
            is ShieldTransaction.Success -> stringRes(R.string.transaction_history_shielded)
            is ShieldTransaction.Pending -> stringRes(R.string.transaction_history_shielding)
            is ShieldTransaction.Failed -> stringRes(R.string.transaction_history_shielding_failed)
            is SendTransaction -> {
                val swapMetadata = data.metadata.swapMetadata
                if (swapMetadata != null && swapMetadata.provider.token.lowercase() == "zec") {
                    stringRes(R.string.transaction_history_swapped)
                } else if (swapMetadata != null) {
                    stringRes(R.string.transaction_history_paid)
                } else {
                    when (transaction) {
                        is SendTransaction.Success -> stringRes(R.string.transaction_history_sent)
                        is SendTransaction.Pending -> stringRes(R.string.transaction_history_sending)
                        is SendTransaction.Failed -> stringRes(R.string.transaction_history_sending_failed)
                    }
                }
            }
        }

    private fun getSubtitle(timestamp: Instant?): StringResource? {
        if (timestamp == null) return null
        val transactionDate = timestamp.atZone(ZoneId.systemDefault())
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

    private fun isShielded(data: ActivityData.ByTransaction) =
        data.transaction
            .transactionOutputs
            .none { output -> output.pool == TransactionPool.TRANSPARENT } &&
            data.transaction !is ShieldTransaction

    private fun getValue(data: ActivityData.ByTransaction) =
        when (data.transaction) {
            is SendTransaction.Success,
            is SendTransaction.Pending ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(data.transaction.amount, HIDDEN),
                    ),
                    StringResourceColor.PRIMARY
                )

            is SendTransaction.Failed ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_minus,
                        stringRes(data.transaction.amount, HIDDEN)
                    ),
                    StringResourceColor.NEGATIVE
                )

            is ReceiveTransaction.Success ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(data.transaction.amount, HIDDEN)
                    ),
                    StringResourceColor.POSITIVE
                )

            is ReceiveTransaction.Pending ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(data.transaction.amount, HIDDEN)
                    ),
                    StringResourceColor.PRIMARY
                )

            is ReceiveTransaction.Failed ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(data.transaction.amount, HIDDEN)
                    ),
                    StringResourceColor.NEGATIVE
                )

            is ShieldTransaction.Success,
            is ShieldTransaction.Pending ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(data.transaction.amount, HIDDEN)
                    ),
                    StringResourceColor.PRIMARY
                )

            is ShieldTransaction.Failed ->
                styledStringResource(
                    stringRes(
                        R.string.transaction_history_plus,
                        stringRes(data.transaction.amount, HIDDEN)
                    ),
                    StringResourceColor.NEGATIVE
                )
        }
}

private const val MONTH_THRESHOLD = 30
