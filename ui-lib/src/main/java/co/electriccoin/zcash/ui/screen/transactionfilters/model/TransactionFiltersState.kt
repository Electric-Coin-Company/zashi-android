package co.electriccoin.zcash.ui.screen.transactionfilters.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class TransactionFiltersState(
    val onBack: () -> Unit,
    val filters: List<TransactionFilterState>,
    val primaryButton: ButtonState,
    val secondaryButton: ButtonState
)

data class TransactionFilterState(
    val type: TransactionFilterType,
    val text: StringResource,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

enum class TransactionFilterType {
    SENT,
    RECEIVED,
    MEMOS,
    UNREAD,
    BOOKMARKED,
    NOTES
}
