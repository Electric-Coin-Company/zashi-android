package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class SwapSlippageState(
    val picker: SlippagePickerState,
    val info: SwapSlippageInfoState?,
    val footer: StringResource?,
    val primary: ButtonState,
    override val onBack: () -> Unit
) : ModalBottomSheetState

@Immutable
data class SwapSlippageInfoState(
    val title: StringResource,
    val mode: Mode,
) {
    enum class Mode { LOW, MEDIUM, HIGH }
}
