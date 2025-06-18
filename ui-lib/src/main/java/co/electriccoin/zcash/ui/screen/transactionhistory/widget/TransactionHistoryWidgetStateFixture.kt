package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.TransactionStateFixture

object TransactionHistoryWidgetStateFixture {
    fun new() =
        TransactionHistoryWidgetState.Data(
            header =
                TransactionHistoryWidgetHeaderState(
                    title = stringRes("Transactions"),
                    button =
                        ButtonState(
                            text = stringRes("See All"),
                            trailingIcon = co.electriccoin.zcash.ui.design.R.drawable.ic_chevron_right_small,
                            onClick = {}
                        )
                ),
            transactions =
                listOf(
                    TransactionStateFixture.new(),
                    TransactionStateFixture.new(),
                    TransactionStateFixture.new(),
                )
        )
}
