package co.electriccoin.zcash.ui.screen.transactionhistory

import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource

data class TransactionHistoryState(
    val search: TextFieldState,
    val filterButton: TransactionFilterButtonState,
    val items: List<TransactionHistoryItem>,
)

data class TransactionFilterButtonState(
    val badge: StringResource?,
    val onClick: () -> Unit,
)

sealed interface TransactionHistoryItem {
    data class Header(
        val title: StringResource,
    ) : TransactionHistoryItem

    data class Transaction(
        val state: TransactionState
    ) : TransactionHistoryItem
}
