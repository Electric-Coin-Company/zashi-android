package co.electriccoin.zcash.ui.screen.account.history.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.TransactionRecipient
import co.electriccoin.zcash.ui.screen.account.model.HistoryItemExpandableState
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal object TransactionHistoryUiStateFixture {
    val TRANSACTIONS =
        persistentListOf(
            TransactionUi(
                TransactionOverviewFixture.new(),
                TransactionRecipient.Account(Account.DEFAULT),
                HistoryItemExpandableState.COLLAPSED
            ),
            TransactionUi(
                TransactionOverviewFixture.new(),
                TransactionRecipient.Account(Account(1)),
                HistoryItemExpandableState.EXPANDED
            ),
            TransactionUi(
                TransactionOverviewFixture.new(),
                null,
                HistoryItemExpandableState.COLLAPSED
            ),
        )
    val STATE = TransactionUiState.Prepared(TRANSACTIONS)

    fun new(
        transactions: ImmutableList<TransactionUi> = TRANSACTIONS,
        state: TransactionUiState = STATE
    ) = when (state) {
        is TransactionUiState.Loading -> state
        is TransactionUiState.Syncing -> state
        is TransactionUiState.Prepared -> {
            state.copy(transactions)
        }
    }
}
