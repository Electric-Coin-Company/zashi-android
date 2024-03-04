package co.electriccoin.zcash.ui.screen.account.history.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.TransactionOverview
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal object TransactionHistorySyncStateFixture {
    val TRANSACTIONS =
        persistentListOf(
            TransactionOverviewFixture.new(),
            TransactionOverviewFixture.new(),
            TransactionOverviewFixture.new()
        )
    val STATE = TransactionHistorySyncState.Syncing(TRANSACTIONS)

    fun new(
        transactions: ImmutableList<TransactionOverview> = TRANSACTIONS,
        state: TransactionHistorySyncState = STATE
    ) = when (state) {
        is TransactionHistorySyncState.Syncing -> {
            state.copy(transactions)
        }
        is TransactionHistorySyncState.Done -> {
            state.copy(transactions)
        }
        TransactionHistorySyncState.Loading -> {
            state
        }
    }
}
