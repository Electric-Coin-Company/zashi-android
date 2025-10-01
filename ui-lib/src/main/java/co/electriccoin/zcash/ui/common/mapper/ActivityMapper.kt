package co.electriccoin.zcash.ui.common.mapper

import cash.z.ecc.android.sdk.model.TransactionPool
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.model.SwapStatus.EXPIRED
import co.electriccoin.zcash.ui.common.model.SwapStatus.FAILED
import co.electriccoin.zcash.ui.common.model.SwapStatus.INCOMPLETE_DEPOSIT
import co.electriccoin.zcash.ui.common.model.SwapStatus.PENDING
import co.electriccoin.zcash.ui.common.model.SwapStatus.PROCESSING
import co.electriccoin.zcash.ui.common.model.SwapStatus.REFUNDED
import co.electriccoin.zcash.ui.common.model.SwapStatus.SUCCESS
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.SendTransaction
import co.electriccoin.zcash.ui.common.repository.ShieldTransaction
import co.electriccoin.zcash.ui.common.repository.Transaction
import co.electriccoin.zcash.ui.common.usecase.ActivityData
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StringResourceColor
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.styledStringResource
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ActivityMapper {
    fun createTransactionState(
        data: ActivityData,
        restoreTimestamp: Instant,
        onTransactionClick: (Transaction) -> Unit,
        onSwapClick: (depositAddress: String) -> Unit
    ): TransactionState =
        when (data) {
            is ActivityData.BySwap ->
                TransactionState(
                    key = data.swap.depositAddress,
                    bigIcon = getSwapBigIcon(data),
                    smallIcon = null,
                    title = getSwapTitle(data),
                    subtitle = getSubtitle(data.swap.lastUpdated),
                    isShielded = false,
                    value = getSwapValue(data),
                    onClick = { onSwapClick(data.swap.depositAddress) },
                    isUnread = false
                )

            is ActivityData.ByTransaction ->
                TransactionState(
                    key = data.transaction.id.txIdString(),
                    bigIcon = getTransactionBigIcon(data),
                    smallIcon = null,
                    title = getTransactionTitle(data),
                    subtitle = getSubtitle(data.transaction.timestamp),
                    isShielded = isTransactionShielded(data),
                    value = getTransactionValue(data),
                    onClick = { onTransactionClick(data.transaction) },
                    isUnread = isTransactionUnread(data, restoreTimestamp)
                )
        }

    private fun getSwapValue(data: ActivityData.BySwap): StyledStringResource =
        styledStringResource(
            stringResByCurrencyNumber(data.swap.amountOutFormatted, "ZEC"),
            when (data.swap.status) {
                INCOMPLETE_DEPOSIT, PROCESSING, PENDING, SUCCESS -> StringResourceColor.PRIMARY
                EXPIRED, REFUNDED, FAILED -> StringResourceColor.NEGATIVE
            }
        )

    private fun getSwapTitle(data: ActivityData.BySwap): StringResource =
        stringRes(
            when (data.swap.status) {
                INCOMPLETE_DEPOSIT, PROCESSING, PENDING -> R.string.transaction_history_swapping
                SUCCESS -> R.string.transaction_history_swapped
                REFUNDED -> R.string.transaction_history_swap_refunded
                FAILED -> R.string.transaction_history_swap_failed
                EXPIRED -> R.string.transaction_history_swap_expired
            }
        )

    private fun getSwapBigIcon(data: ActivityData.BySwap): Int =
        when (data.swap.status) {
            INCOMPLETE_DEPOSIT, PROCESSING, PENDING -> R.drawable.ic_transaction_receive_pending
            SUCCESS -> R.drawable.ic_transaction_received
            EXPIRED, REFUNDED, FAILED -> R.drawable.ic_transaction_receive_failed
        }

    private fun isTransactionUnread(data: ActivityData.ByTransaction, restoreTimestamp: Instant): Boolean {
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

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    private fun getTransactionBigIcon(data: ActivityData.ByTransaction) =
        when (val transaction = data.transaction) {
            is ReceiveTransaction.Success -> R.drawable.ic_transaction_received
            is ReceiveTransaction.Pending -> R.drawable.ic_transaction_receive_pending
            is ReceiveTransaction.Failed -> R.drawable.ic_transaction_receive_failed
            is ShieldTransaction.Success -> R.drawable.ic_transaction_shielded
            is ShieldTransaction.Pending -> R.drawable.ic_transaction_shield_pending
            is ShieldTransaction.Failed -> R.drawable.ic_transaction_shield_failed
            is SendTransaction -> {
                if (data.metadata.swapMetadata == null) {
                    when (transaction) {
                        is SendTransaction.Success -> R.drawable.ic_transaction_sent
                        is SendTransaction.Pending -> R.drawable.ic_transaction_send_pending
                        is SendTransaction.Failed -> R.drawable.ic_transaction_send_failed
                    }
                } else {
                    if (transaction is SendTransaction.Failed) {
                        when (data.metadata.swapMetadata.mode) {
                            EXACT_INPUT -> R.drawable.ic_transaction_send_failed
                            EXACT_OUTPUT -> R.drawable.ic_transaction_pay_failed
                        }
                    } else {
                        when (data.metadata.swapMetadata.mode) {
                            EXACT_INPUT ->
                                when (data.metadata.swapMetadata.status) {
                                    INCOMPLETE_DEPOSIT, PROCESSING, PENDING -> R.drawable.ic_transaction_send_pending
                                    SUCCESS -> R.drawable.ic_transaction_sent
                                    EXPIRED, REFUNDED, FAILED -> R.drawable.ic_transaction_send_failed
                                }

                            EXACT_OUTPUT ->
                                when (data.metadata.swapMetadata.status) {
                                    INCOMPLETE_DEPOSIT, PROCESSING, PENDING -> R.drawable.ic_transaction_paying
                                    SUCCESS -> R.drawable.ic_transaction_paid
                                    EXPIRED, REFUNDED, FAILED -> R.drawable.ic_transaction_pay_failed
                                }
                        }
                    }
                }
            }
        }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    private fun getTransactionTitle(data: ActivityData.ByTransaction) =
        when (val transaction = data.transaction) {
            is ReceiveTransaction.Success -> stringRes(R.string.transaction_history_received)
            is ReceiveTransaction.Pending -> stringRes(R.string.transaction_history_receiving)
            is ReceiveTransaction.Failed -> stringRes(R.string.transaction_history_receiving_failed)
            is ShieldTransaction.Success -> stringRes(R.string.transaction_history_shielded)
            is ShieldTransaction.Pending -> stringRes(R.string.transaction_history_shielding)
            is ShieldTransaction.Failed -> stringRes(R.string.transaction_history_shielding_failed)
            is SendTransaction -> {
                if (data.metadata.swapMetadata == null) {
                    when (transaction) {
                        is SendTransaction.Success -> stringRes(R.string.transaction_history_sent)
                        is SendTransaction.Pending -> stringRes(R.string.transaction_history_sending)
                        is SendTransaction.Failed -> stringRes(R.string.transaction_history_sending_failed)
                    }
                } else {
                    if (transaction is SendTransaction.Failed) {
                        when (data.metadata.swapMetadata.mode) {
                            EXACT_INPUT -> stringRes(R.string.transaction_history_swap_failed)
                            EXACT_OUTPUT -> stringRes(R.string.transaction_history_payment_failed)
                        }
                    } else {
                        when (data.metadata.swapMetadata.mode) {
                            EXACT_INPUT ->
                                when (data.metadata.swapMetadata.status) {
                                    INCOMPLETE_DEPOSIT,
                                    PROCESSING,
                                    PENDING -> stringRes(R.string.transaction_history_swapping)

                                    SUCCESS -> stringRes(R.string.transaction_history_swapped)
                                    REFUNDED -> stringRes(R.string.transaction_history_swap_refunded)
                                    FAILED -> stringRes(R.string.transaction_history_swap_failed)
                                    EXPIRED -> stringRes(R.string.transaction_history_swap_expired)
                                }

                            EXACT_OUTPUT ->
                                when (data.metadata.swapMetadata.status) {
                                    INCOMPLETE_DEPOSIT,
                                    PROCESSING,
                                    PENDING -> stringRes(R.string.transaction_history_paying)

                                    SUCCESS -> stringRes(R.string.transaction_history_paid)
                                    REFUNDED -> stringRes(R.string.transaction_history_payment_refunded)
                                    FAILED -> stringRes(R.string.transaction_history_payment_failed)
                                    EXPIRED -> stringRes(R.string.transaction_history_payment_expired)
                                }
                        }
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

    @Suppress("CyclomaticComplexMethod")
    private fun isTransactionShielded(data: ActivityData.ByTransaction) =
        data.transaction
            .transactionOutputs
            .none { output -> output.pool == TransactionPool.TRANSPARENT } &&
            data.transaction !is ShieldTransaction

    @Suppress("CyclomaticComplexMethod")
    private fun getTransactionValue(data: ActivityData.ByTransaction): StyledStringResource {
        val stringRes =
            when (data.transaction) {
                is SendTransaction.Success,
                is SendTransaction.Failed,
                is SendTransaction.Pending ->
                    stringRes("- ") + stringRes(data.transaction.amount)

                is ShieldTransaction.Success,
                is ShieldTransaction.Failed,
                is ShieldTransaction.Pending,
                is ReceiveTransaction.Success,
                is ReceiveTransaction.Failed,
                is ReceiveTransaction.Pending ->
                    stringRes(data.transaction.amount)
            }

        val color =
            when (data.transaction) {
                is ReceiveTransaction.Success -> StringResourceColor.POSITIVE
                is ReceiveTransaction.Pending -> StringResourceColor.PRIMARY
                is ReceiveTransaction.Failed -> StringResourceColor.NEGATIVE
                is ShieldTransaction.Success -> StringResourceColor.PRIMARY
                is ShieldTransaction.Pending -> StringResourceColor.PRIMARY
                is ShieldTransaction.Failed -> StringResourceColor.NEGATIVE
                is SendTransaction ->
                    when {
                        data.metadata.swapMetadata == null ->
                            when (data.transaction) {
                                is SendTransaction.Success,
                                is SendTransaction.Pending -> StringResourceColor.PRIMARY

                                is SendTransaction.Failed -> StringResourceColor.NEGATIVE
                            }

                        data.transaction is SendTransaction.Failed -> StringResourceColor.NEGATIVE
                        else ->
                            when (data.metadata.swapMetadata.status) {
                                INCOMPLETE_DEPOSIT,
                                PENDING,
                                SUCCESS,
                                PROCESSING -> StringResourceColor.PRIMARY

                                EXPIRED,
                                REFUNDED,
                                FAILED -> StringResourceColor.NEGATIVE
                            }
                    }
            }

        return styledStringResource(stringRes, color)
    }
}

private const val MONTH_THRESHOLD = 30
