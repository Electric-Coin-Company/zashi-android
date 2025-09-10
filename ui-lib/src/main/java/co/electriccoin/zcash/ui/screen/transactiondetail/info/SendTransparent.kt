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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.orHidden
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.SendTransparentStateFixture
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailHeader
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumn
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumnState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoContainer
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRow
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRowState

@Composable
fun SendTransparent(
    state: SendTransparentState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        var isExpanded by rememberSaveable { mutableStateOf(false) }

        TransactionDetailHeader(
            title = stringRes(R.string.transaction_detail_info_transaction_details),
            isExpanded = isExpanded,
            onButtonClick = { isExpanded = !isExpanded }
        )

        Spacer(Modifier.height(8.dp))
        TransactionDetailInfoContainer {
            TransactionDetailInfoRow(
                modifier = Modifier.fillMaxWidth(),
                state =
                    TransactionDetailInfoRowState(
                        title = stringRes(R.string.transaction_detail_info_sent_to),
                        message =
                            state.contact
                                ?: (
                                    state.addressAbbreviated orHidden
                                        stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder)
                                ),
                        trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                        onClick = state.onTransactionAddressClick
                    )
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column {
                    ZashiHorizontalDivider()
                    TransactionDetailInfoRow(
                        modifier = Modifier.fillMaxWidth(),
                        state =
                            TransactionDetailInfoRowState(
                                title = stringRes(R.string.transaction_detail_info_transaction_id),
                                message =
                                    state.transactionId orHidden
                                        stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder),
                                trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                                onClick = state.onTransactionIdClick
                            )
                    )
                    ZashiHorizontalDivider()
                    TransactionDetailInfoRow(
                        modifier = Modifier.fillMaxWidth(),
                        state =
                            TransactionDetailInfoRowState(
                                title = stringRes(R.string.transaction_detail_info_transaction_fee),
                                message =
                                    state.fee orHidden
                                        stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder),
                            )
                    )
                    ZashiHorizontalDivider()
                    TransactionDetailInfoRow(
                        modifier = Modifier.fillMaxWidth(),
                        state =
                            TransactionDetailInfoRowState(
                                title =
                                    if (state.isPending) {
                                        stringRes(R.string.transaction_detail_info_transaction_status)
                                    } else {
                                        stringRes(R.string.transaction_detail_info_transaction_completed)
                                    },
                                message = if (state.isPending) {
                                    state.completedTimestamp
                                } else {
                                    state.completedTimestamp orHidden
                                        stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder)
                                }
                            )
                    )
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
                            onClick = null
                        )
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
            SendTransparent(
                state = SendTransparentStateFixture.new(),
            )
        }
    }

@PreviewScreens
@Composable
private fun PreviewWithoutContact() =
    ZcashTheme {
        BlankSurface {
            SendTransparent(
                state = SendTransparentStateFixture.new(contact = null),
            )
        }
    }
