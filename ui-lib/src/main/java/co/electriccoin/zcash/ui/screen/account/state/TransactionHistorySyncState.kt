package co.electriccoin.zcash.ui.screen.account.state

import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt
import kotlinx.collections.immutable.ImmutableList

sealed interface TransactionHistorySyncState {
    data object Loading : TransactionHistorySyncState

    data class Syncing(val transactions: ImmutableList<TransactionOverviewExt>) : TransactionHistorySyncState

    data class Done(val transactions: ImmutableList<TransactionOverviewExt>) : TransactionHistorySyncState
}
