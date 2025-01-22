package co.electriccoin.zcash.ui.screen.transactionhistory

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.util.stringRes
import java.util.UUID

object TransactionStateFixture {

    @Suppress("MagicNumber")
    fun new() =
        TransactionState(
            icon = R.drawable.ic_transaction_sent,
            title = stringRes("Transaction Title"),
            subtitle = stringRes("Transaction subtitle"),
            isShielded = true,
            value = stringRes(R.string.transaction_history_plus, stringRes(Zatoshi(10000000))),
            onClick = {},
            key = UUID.randomUUID().toString()
        )
}
