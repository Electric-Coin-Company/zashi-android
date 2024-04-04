package co.electriccoin.zcash.ui.screen.account.model

import kotlinx.collections.immutable.ImmutableList

sealed interface TransactionUiState {
    data object Loading : TransactionUiState

    data object SyncingEmpty : TransactionUiState

    data object DoneEmpty : TransactionUiState

    sealed class Prepared(open val transactions: ImmutableList<TransactionUi>) : TransactionUiState

    data class Syncing(override val transactions: ImmutableList<TransactionUi>) : Prepared(transactions)

    data class Done(override val transactions: ImmutableList<TransactionUi>) : Prepared(transactions)
}
