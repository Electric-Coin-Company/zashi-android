package co.electriccoin.zcash.ui.screen.account.model

import kotlinx.collections.immutable.ImmutableList

sealed interface TransactionUiState {
    data object Loading : TransactionUiState

    data object Syncing : TransactionUiState

    data class Prepared(val transactions: ImmutableList<TransactionUi>) : TransactionUiState
}
