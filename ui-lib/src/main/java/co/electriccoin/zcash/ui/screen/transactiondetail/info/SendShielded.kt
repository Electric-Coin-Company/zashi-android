package co.electriccoin.zcash.ui.screen.transactiondetail.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.SendShieldStateFixture
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumn
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumnState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoHeader
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoHeaderState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRow
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRowState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailMemo

@Composable
fun SendShielded(
    state: SendShieldedState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        TransactionDetailInfoHeader(
            state =
                TransactionDetailInfoHeaderState(
                    title = stringRes(R.string.transaction_detail_info_transaction_details)
                )
        )

        Spacer(Modifier.height(8.dp))

        var isExpanded by rememberSaveable { mutableStateOf(false) }

        TransactionDetailInfoRow(
            modifier = Modifier.fillMaxWidth(),
            state =
                TransactionDetailInfoRowState(
                    title = stringRes(R.string.transaction_detail_info_sent_to),
                    message =
                        when {
                            state.contact != null -> state.contact
                            !isExpanded -> state.addressAbbreviated
                            else -> null
                        },
                    trailingIcon = if (isExpanded) R.drawable.ic_chevron_up_small else R.drawable.ic_chevron_down_small,
                    shape = if (isExpanded) TransactionDetailInfoShape.FIRST else TransactionDetailInfoShape.SINGLE,
                    onClick = { isExpanded = !isExpanded }
                )
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                TransactionDetailInfoColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state =
                        TransactionDetailInfoColumnState(
                            title =
                                if (state.contact == null) {
                                    null
                                } else {
                                    stringRes(R.string.transaction_detail_info_address)
                                },
                            message = state.address,
                            shape = TransactionDetailInfoShape.MIDDLE,
                            onClick = state.onTransactionAddressClick,
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
                            shape = TransactionDetailInfoShape.MIDDLE,
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
                            shape = TransactionDetailInfoShape.MIDDLE,
                        )
                )
                ZashiHorizontalDivider()
                TransactionDetailInfoRow(
                    modifier = Modifier.fillMaxWidth(),
                    state =
                        TransactionDetailInfoRowState(
                            title = stringRes(R.string.transaction_detail_info_transaction_completed),
                            message = state.completedTimestamp,
                            shape = TransactionDetailInfoShape.LAST,
                        )
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        TransactionDetailInfoHeader(
            state =
                TransactionDetailInfoHeaderState(
                    title = stringRes(R.string.transaction_detail_info_message)
                )
        )
        Spacer(Modifier.height(8.dp))
        TransactionDetailMemo(
            modifier = Modifier.fillMaxWidth(),
            state = state.memo
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SendShielded(
                state = SendShieldStateFixture.new(),
            )
        }
    }

@PreviewScreens
@Composable
private fun PreviewWithoutContact() =
    ZcashTheme {
        BlankSurface {
            SendShielded(
                state = SendShieldStateFixture.new(contact = null),
            )
        }
    }
