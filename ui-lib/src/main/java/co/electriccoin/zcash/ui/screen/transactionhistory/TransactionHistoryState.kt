package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource
import java.util.UUID

data class TransactionHistoryState(
    val onBack: () -> Unit,
    val search: TextFieldState,
    val filterButton: IconButtonState,
    val items: List<TransactionHistoryItem>,
)

@Immutable
sealed interface TransactionHistoryItem : Itemizable {
    @Immutable
    data class Header(
        val title: StringResource,
        override val key: Any = UUID.randomUUID(),
    ) : TransactionHistoryItem {
        override val contentType = "Transaction Header"
    }

    @Immutable
    data class Transaction(
        val state: TransactionState
    ) : TransactionHistoryItem {
        override val contentType = state.contentType
        override val key: Any = state.key
    }
}
