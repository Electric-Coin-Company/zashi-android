package co.electriccoin.zcash.ui.screen.account.state

import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt
import kotlinx.collections.immutable.ImmutableList

sealed interface TransactionHistorySyncState {
    data object Loading : TransactionHistorySyncState

    sealed class Prepared(open val transactions: ImmutableList<TransactionOverviewExt>) : TransactionHistorySyncState

    data class Syncing(override val transactions: ImmutableList<TransactionOverviewExt>) : Prepared(transactions)

    data class Done(override val transactions: ImmutableList<TransactionOverviewExt>) : Prepared(transactions)
}
