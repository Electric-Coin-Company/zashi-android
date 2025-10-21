package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionhistory.ActivityStateFixture

object ActivityWidgetStateFixture {
    fun new() =
        ActivityWidgetState.Data(
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
                    ActivityStateFixture.new(),
                    ActivityStateFixture.new(),
                    ActivityStateFixture.new(),
                )
        )
}
