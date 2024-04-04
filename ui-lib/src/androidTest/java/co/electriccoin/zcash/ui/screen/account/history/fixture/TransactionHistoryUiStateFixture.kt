package co.electriccoin.zcash.ui.screen.account.history.fixture

import co.electriccoin.zcash.ui.screen.account.fixture.TransactionsFixture
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import kotlinx.collections.immutable.ImmutableList

internal object TransactionHistoryUiStateFixture {
    val TRANSACTIONS = TransactionsFixture.new()
    val STATE = TransactionUiState.Done(TRANSACTIONS)

    fun new(
        transactions: ImmutableList<TransactionUi> = TRANSACTIONS,
        state: TransactionUiState = STATE
    ) = when (state) {
        is TransactionUiState.Loading -> state
        is TransactionUiState.Syncing -> state.copy(transactions)
        is TransactionUiState.Done -> state.copy(transactions)
        TransactionUiState.DoneEmpty -> state
        TransactionUiState.SyncingEmpty -> state
    }
}
