package co.electriccoin.zcash.ui.screen.transactiondetail

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class TransactionDetailState(
    val onBack: () -> Unit,
    val bookmarkButton: IconButtonState,
    val icons: List<Int>,
    val title: StringResource,
    val subtitle: StringResource,
    val items: List<TransactionDetailItem>,
    val primaryButton: ButtonState?,
    val secondaryButton: ButtonState
)

sealed interface TransactionDetailItem {
    data class Header(
        val title: StringResource
    ) : TransactionDetailItem

    data class Row(
        val title: StringResource,
        val message: StringResource,
        val onClick: () -> Unit,
    ) : TransactionDetailItem

    data class ExpandableRow(
        val title: StringResource,
        val message: StringResource,
        val expandedTitle: StringResource?,
        val expandedMessage: StringResource,
        val onClick: () -> Unit,
    ) : TransactionDetailItem

    data class Memo(
        val content: StringResource
    ) : TransactionDetailItem

    data class Note(
        val content: StringResource
    ) : TransactionDetailItem
}
