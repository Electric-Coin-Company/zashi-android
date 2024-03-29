package co.electriccoin.zcash.ui.screen.account.fixture

import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

object TransactionsFixture {
    val TRANSACTIONS: ImmutableList<TransactionUi> =
        persistentListOf(
            TransactionUiFixture.new(),
            TransactionUiFixture.new(),
            TransactionUiFixture.new(),
            TransactionUiFixture.new()
        )

    internal fun new() = TRANSACTIONS
}
