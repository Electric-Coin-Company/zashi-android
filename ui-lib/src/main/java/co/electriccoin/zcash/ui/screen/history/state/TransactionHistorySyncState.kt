package co.electriccoin.zcash.ui.screen.history.state

import cash.z.ecc.android.sdk.model.TransactionOverview
import kotlinx.collections.immutable.ImmutableList

sealed class TransactionHistorySyncState {
    object Loading : TransactionHistorySyncState() {
        override fun toString() = "Loading" // NON-NLS
    }

    data class Syncing(val transactions: ImmutableList<TransactionOverview>) : TransactionHistorySyncState() {
        fun hasNoTransactions(): Boolean {
            return transactions.isEmpty()
        }
    }

    data class Done(val transactions: ImmutableList<TransactionOverview>) : TransactionHistorySyncState() {
        fun hasNoTransactions(): Boolean {
            return transactions.isEmpty()
        }
    }
}
