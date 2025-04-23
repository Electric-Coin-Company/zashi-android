package co.electriccoin.zcash.ui.screen.transactiondetail.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.ReceiveShieldedStateFixture
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumn
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoColumnState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoHeader
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoHeaderState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRow
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRowState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailMemo

@Composable
fun ReceiveShielded(
    state: ReceiveShieldedState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        state.memo?.let {
            TransactionDetailInfoHeader(
                state =
                    TransactionDetailInfoHeaderState(
                        title = stringRes(R.string.transaction_detail_info_message)
                    )
            )
            Spacer(Modifier.height(8.dp))
            TransactionDetailMemo(
                modifier = Modifier.fillMaxWidth(),
                state = it
            )
        }
        Spacer(Modifier.height(20.dp))
        TransactionDetailInfoHeader(
            state =
                TransactionDetailInfoHeaderState(
                    title = stringRes(R.string.transaction_detail_info_transaction_details)
                )
        )
        Spacer(Modifier.height(8.dp))
        TransactionDetailInfoRow(
            modifier = Modifier.fillMaxWidth(),
            state =
                TransactionDetailInfoRowState(
                    title = stringRes(R.string.transaction_detail_info_transaction_id),
                    message = state.transactionId,
                    trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                    shape = TransactionDetailInfoShape.FIRST,
                    onClick = state.onTransactionIdClick
                )
        )
        ZashiHorizontalDivider()
        TransactionDetailInfoRow(
            modifier = Modifier.fillMaxWidth(),
            state =
                TransactionDetailInfoRowState(
                    title = if (state.isPending) {
                        stringRes(R.string.transaction_detail_info_transaction_status)
                    } else {
                        stringRes(R.string.transaction_detail_info_transaction_completed)
                    },
                    message = state.completedTimestamp,
                    shape =
                        if (state.note != null) {
                            TransactionDetailInfoShape.MIDDLE
                        } else {
                            TransactionDetailInfoShape.LAST
                        },
                )
        )
        if (state.note != null) {
            ZashiHorizontalDivider()
            TransactionDetailInfoColumn(
                modifier = Modifier.fillMaxWidth(),
                state =
                    TransactionDetailInfoColumnState(
                        title = stringRes(R.string.transaction_detail_info_note),
                        message = state.note,
                        shape = TransactionDetailInfoShape.LAST,
                        onClick = null
                    )
            )
        }
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ReceiveShielded(
                state = ReceiveShieldedStateFixture.new(),
            )
        }
    }

@PreviewScreens
@Composable
private fun PreviewWithoutMemo() =
    ZcashTheme {
        BlankSurface {
            ReceiveShielded(
                state = ReceiveShieldedStateFixture.new(memo = TransactionDetailMemosState(emptyList())),
            )
        }
    }
