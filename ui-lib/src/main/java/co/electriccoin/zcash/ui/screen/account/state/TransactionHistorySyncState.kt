package co.electriccoin.zcash.ui.screen.account.state

import kotlinx.collections.immutable.ImmutableList

sealed class TransactionHistorySyncState {
    object Loading : TransactionHistorySyncState() {
        override fun toString() = "Loading" // NON-NLS
    }

    data class Syncing(val transactions: ImmutableList<TransactionOverviewExt>) : TransactionHistorySyncState() {
        fun hasNoTransactions(): Boolean {
            return transactions.isEmpty()
        }
    }

    data class Done(val transactions: ImmutableList<TransactionOverviewExt>) : TransactionHistorySyncState() {
        fun hasNoTransactions(): Boolean {
            return transactions.isEmpty()
        }
    }
}
