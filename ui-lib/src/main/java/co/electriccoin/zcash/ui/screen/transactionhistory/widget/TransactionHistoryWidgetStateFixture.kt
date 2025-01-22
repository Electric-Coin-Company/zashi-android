package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionState

object TransactionHistoryWidgetStateFixture {
    fun new() = TransactionHistoryWidgetState.Data(
        header = TransactionHistoryWidgetHeaderState(
            title = stringRes("Transactions"),
            button = ButtonState(
                text = stringRes("See All"),
                trailingIcon = R.drawable.ic_chevron_right_small,
                onClick = {}
            )
        ),
        transactions = listOf(
            TransactionState(
                icon = R.drawable.ic_transaction_sent,
                title = stringRes("Transaction Title"),
                subtitle = stringRes("Transaction subtitle"),
                isShielded = true,
                value = stringRes(R.string.transaction_history_plus, stringRes(Zatoshi(10000000))),
                onClick = {}
            ),
            TransactionState(
                icon = R.drawable.ic_transaction_sent,
                title = stringRes("Transaction Title"),
                subtitle = stringRes("Transaction subtitle"),
                isShielded = true,
                value = stringRes(R.string.transaction_history_plus, stringRes(Zatoshi(10000000))),
                onClick = {}
            ),
            TransactionState(
                icon = R.drawable.ic_transaction_sent,
                title = stringRes("Transaction Title"),
                subtitle = stringRes("Transaction subtitle"),
                isShielded = true,
                value = stringRes(R.string.transaction_history_plus, stringRes(Zatoshi(10000000))),
                onClick = {}
            )
        )
    )
}