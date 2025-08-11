package co.electriccoin.zcash.ui.screen.transactiondetail.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.SwapQuoteHeaderState
import co.electriccoin.zcash.ui.design.component.ZashiCard
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSwapQuoteHeader
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailHeader
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumn
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumnState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRow
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRowState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape.FIRST
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape.LAST
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape.MIDDLE
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailSwapStatusRow
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailSwapStatusRowState

@Composable
fun SendSwap(
    state: SendSwapState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        var isExpanded by rememberSaveable { mutableStateOf(false) }

        ZashiSwapQuoteHeader(state = state.quoteHeader)
        Spacer(20.dp)
        TransactionDetailHeader(
            title = stringRes(R.string.transaction_detail_info_transaction_details),
            isExpanded = isExpanded,
            onButtonClick = { isExpanded = !isExpanded }
        )
        Spacer(8.dp)
        TransactionDetailSwapStatusRow(
            modifier = Modifier.fillMaxWidth(),
            state =
                TransactionDetailSwapStatusRowState(
                    title = stringRes(R.string.transaction_detail_info_transaction_status),
                    shape = FIRST,
                    status = state.status
                )
        )
        ZashiHorizontalDivider()
        TransactionDetailInfoRow(
            modifier = Modifier.fillMaxWidth(),
            state =
                TransactionDetailInfoRowState(
                    title = stringRes(R.string.transaction_detail_info_sent_to),
                    message = state.depositAddress,
                    trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                    shape =
                        when {
                            state.note != null -> MIDDLE
                            isExpanded -> MIDDLE
                            else -> LAST
                        },
                    onClick = state.onDepositAddressClick
                ),
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                ZashiHorizontalDivider()
                TransactionDetailInfoRow(
                    modifier = Modifier.fillMaxWidth(),
                    state =
                        TransactionDetailInfoRowState(
                            title = stringRes("Recipient"),
                            message = state.recipientAddress,
                            trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                            shape = MIDDLE,
                            onClick = state.onRecipientAddressClick
                        )
                )
                ZashiHorizontalDivider()
                TransactionDetailInfoRow(
                    modifier = Modifier.fillMaxWidth(),
                    state =
                        TransactionDetailInfoRowState(
                            title = stringRes(R.string.transaction_detail_info_transaction_id),
                            message = state.transactionId,
                            trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                            shape = MIDDLE,
                            onClick = state.onTransactionIdClick
                        )
                )
                ZashiHorizontalDivider()
                TransactionDetailInfoRow(
                    modifier = Modifier.fillMaxWidth(),
                    state =
                        TransactionDetailInfoRowState(
                            title = stringRes(R.string.transaction_detail_info_transaction_fee),
                            message = state.fee,
                            shape = MIDDLE,
                        )
                )
                ZashiHorizontalDivider()
                TransactionDetailInfoRow(
                    modifier = Modifier.fillMaxWidth(),
                    state =
                        TransactionDetailInfoRowState(
                            title = if (state.isSlippageRealized) {
                                stringRes("Realized slippage")
                            } else {
                                stringRes("Max slippage")
                            },
                            message = state.maxSlippage,
                            shape = when {
                                state.status == SwapStatus.REFUNDED -> MIDDLE
                                state.note != null -> MIDDLE
                                else -> LAST
                            },
                        )
                )
                if (state.status == SwapStatus.REFUNDED) {
                    ZashiHorizontalDivider()
                    TransactionDetailInfoRow(
                        modifier = Modifier.fillMaxWidth(),
                        state =
                            TransactionDetailInfoRowState(
                                title = stringRes("Refunded amount"),
                                message = state.fee,
                                shape = if (state.note != null) MIDDLE else LAST,
                            )
                    )
                }
            }
        }
        if (state.note != null) {
            ZashiHorizontalDivider()
            TransactionDetailInfoColumn(
                modifier = Modifier.fillMaxWidth(),
                state =
                    TransactionDetailInfoColumnState(
                        title = stringRes(R.string.transaction_detail_info_note),
                        message = state.note,
                        shape = LAST,
                        onClick = null
                    )
            )
        }
        if (state.status == SwapStatus.REFUNDED) {
            Spacer(8.dp)
            RefundedInfo()
        }
    }
}

@Composable
private fun RefundedInfo() {
    ZashiCard(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = ZashiColors.Utility.WarningYellow.utilityOrange50,
                contentColor = ZashiColors.Utility.WarningYellow.utilityOrange800,
            ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row {
            Image(
                painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_info),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ZashiColors.Utility.WarningYellow.utilityOrange500)
            )
            Spacer(12.dp)
            Column {
                Spacer(2.dp)
                Text(
                    text = "Payment Refunded",
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Utility.WarningYellow.utilityOrange700
                )
                Spacer(8.dp)
                Text(
                    text = "Your cross-chain payment was sent but the swap was unsuccessful. The payment amount has been refunded, minus transaction fees.",
                    style = ZashiTypography.textXs,
                    color = ZashiColors.Utility.WarningYellow.utilityOrange800
                )
            }
        }
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SendSwap(
                state = SendSwapState(
                    status = SwapStatus.REFUNDED,
                    quoteHeader = SwapQuoteHeaderState(
                        rotateIcon = false,
                        from = null,
                        to = null
                    ),
                    depositAddress = stringResByAddress(value = "Address", abbreviated = true),
                    recipientAddress = null,
                    transactionId = stringRes("Transaction ID"),
                    refundedAmount = stringRes("Refunded amount"),
                    onTransactionIdClick = {},
                    onDepositAddressClick = {},
                    onRecipientAddressClick = {},
                    fee = stringRes(Zatoshi(1011), HIDDEN),
                    maxSlippage = null,
                    note = stringRes("None"),
                    isSlippageRealized = false
                ),
            )
        }
    }
