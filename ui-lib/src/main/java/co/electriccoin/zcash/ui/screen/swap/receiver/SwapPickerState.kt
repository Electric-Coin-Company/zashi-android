package co.electriccoin.zcash.ui.screen.swap.receiver

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState

@Immutable
data class SwapPickerState(
    val search: TextFieldState,
    val items: List<ListItemState>,
    override val onBack: () -> Unit,
) : ModalBottomSheetState
