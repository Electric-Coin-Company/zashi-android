package co.electriccoin.zcash.ui.screen.history.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.TransactionOverview
import co.electriccoin.zcash.ui.screen.history.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal object TransactionHistorySyncStateFixture {
    val TRANSACTIONS = persistentListOf(
        TransactionOverviewFixture.new(id = 0),
        TransactionOverviewFixture.new(id = 1),
        TransactionOverviewFixture.new(id = 2)
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
