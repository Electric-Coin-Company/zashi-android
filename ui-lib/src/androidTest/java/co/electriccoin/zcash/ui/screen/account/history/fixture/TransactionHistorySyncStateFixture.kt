package co.electriccoin.zcash.ui.screen.account.history.fixture

import cash.z.ecc.android.sdk.fixture.AccountFixture
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
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
                TransactionRecipient.RecipientAccount(AccountFixture.new().accountUuid.value),
                AddressType.Shielded,
                emptyList()
            ),
            TransactionOverviewExt(
                TransactionOverviewFixture.new(),
                TransactionRecipient.RecipientAccount(AccountFixture.new().accountUuid.value),
                AddressType.Transparent,
                emptyList()
            ),
            TransactionOverviewExt(
                TransactionOverviewFixture.new(),
                null,
                AddressType.Unified,
                emptyList()
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
