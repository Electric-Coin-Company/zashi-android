package co.electriccoin.zcash.ui.screen.restore.estimation

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class RestoreBDEstimationState(
    val text: StringResource,
    val onBack: () -> Unit,
    val dialogButton: IconButtonState,
    val copy: ButtonState,
    val restore: ButtonState,
)
