package co.electriccoin.zcash.ui.screen.account.history.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal object TransactionHistorySyncStateFixture {
    val TRANSACTIONS =
        persistentListOf(
            TransactionOverviewExt(
                TransactionOverviewFixture.new(),
                TransactionRecipient.Account(Account.DEFAULT),
                AddressType.Shielded
            ),
            TransactionOverviewExt(
                TransactionOverviewFixture.new(),
                TransactionRecipient.Account(Account(1)),
                AddressType.Transparent
            ),
            TransactionOverviewExt(
                TransactionOverviewFixture.new(),
                null,
                AddressType.Unified
            ),
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
