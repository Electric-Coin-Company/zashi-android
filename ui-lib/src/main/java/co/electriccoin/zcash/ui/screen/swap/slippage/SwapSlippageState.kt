package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class SwapSlippageState(
    val slider: SlippageSliderState,
    val customSlippage: NumberTextFieldState?,
    val info: SwapSlippageInfoState?,
    val primary: ButtonState,
    override val onBack: () -> Unit
) : ModalBottomSheetState

@Immutable
data class SwapSlippageInfoState(
    val title: StringResource,
    val description: StringResource,
    val mode: Mode,
) {
    enum class Mode { LOW, MEDIUM, HIGH }
}
