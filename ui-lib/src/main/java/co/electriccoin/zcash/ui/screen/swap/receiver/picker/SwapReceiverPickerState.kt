package co.electriccoin.zcash.ui.screen.swap.receiver.picker

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState

@Immutable
data class SwapReceiverPickerState(
    val search: TextFieldState,
    val data: SwapPickerDataState,
    override val onBack: () -> Unit,
) : ModalBottomSheetState

@Immutable
sealed interface SwapPickerDataState {
    @Immutable
    data object Loading : SwapPickerDataState

    @Immutable
    data class Success(
        val items: List<ListItemState>
    ) : SwapPickerDataState

    @Immutable
    data class Error(
        val button: ButtonState
    ) : SwapPickerDataState
}
