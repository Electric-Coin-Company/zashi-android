package co.electriccoin.zcash.ui.screen.swap.picker

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.screen.home.common.CommonErrorScreenState

@Immutable
data class SwapAssetPickerState(
    val search: TextFieldState,
    val data: SwapAssetPickerDataState,
    override val onBack: () -> Unit,
) : ModalBottomSheetState

@Immutable
sealed interface SwapAssetPickerDataState {
    @Immutable
    data object Loading : SwapAssetPickerDataState

    @Immutable
    data class Success(
        val items: List<ListItemState>
    ) : SwapAssetPickerDataState

    @Immutable
    data class Error(
        override val title: StringResource,
        override val subtitle: StringResource,
        override val buttonState: ButtonState
    ) : SwapAssetPickerDataState,
        CommonErrorScreenState
}
