package co.electriccoin.zcash.ui.screen.transactionhistory

import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource

data class TransactionHistoryState(
    val onBack: () -> Unit,
    val search: TextFieldState,
    val filterButton: IconButtonState,
    val items: List<TransactionHistoryItem>,
)

sealed interface TransactionHistoryItem : Itemizable {
    data class Header(
        override val key: Any,
        val title: StringResource,
    ) : TransactionHistoryItem {
        override val contentType = "Transaction Header"
    }

    data class Transaction(
        val state: TransactionState
    ) : TransactionHistoryItem {
        override val contentType = state.contentType
        override val key: Any = state.key
    }
}
