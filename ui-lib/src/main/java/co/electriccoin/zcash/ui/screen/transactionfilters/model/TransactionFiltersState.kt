package co.electriccoin.zcash.ui.screen.transactionfilters.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

data class TransactionFiltersState(
    val filters: List<TransactionFilterState>,
    override val onBack: () -> Unit,
    val primaryButton: ButtonState,
    val secondaryButton: ButtonState
) : ModalBottomSheetState

data class TransactionFilterState(
    val text: StringResource,
    val isSelected: Boolean,
    val onClick: () -> Unit
)
