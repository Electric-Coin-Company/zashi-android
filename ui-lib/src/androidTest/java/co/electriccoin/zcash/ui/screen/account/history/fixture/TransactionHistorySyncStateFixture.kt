package co.electriccoin.zcash.ui.screen.account.history.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.TransactionRecipient
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.screen.account.state.TransactionOverviewExt
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal object TransactionHistorySyncStateFixture {
    val TRANSACTIONS =
        persistentListOf(
            TransactionOverviewExt(TransactionOverviewFixture.new(), TransactionRecipient.Account(Account.DEFAULT)),
            TransactionOverviewExt(TransactionOverviewFixture.new(), TransactionRecipient.Account(Account(1))),
            TransactionOverviewExt(TransactionOverviewFixture.new(), null),
        )
    val STATE = TransactionHistorySyncState.Syncing(TRANSACTIONS)

    fun new(
        transactions: ImmutableList<TransactionOverviewExt> = TRANSACTIONS,
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
