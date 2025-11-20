package co.electriccoin.zcash.ui.screen.restore.height

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class RestoreBDHeightState(
    val title: StringResource,
    val subtitle: StringResource,
    val message: StringResource,
    val textFieldTitle: StringResource,
    val textFieldHint: StringResource,
    val textFieldNote: StringResource,
    val blockHeight: NumberTextFieldState,
    val estimate: ButtonState,
    val restore: ButtonState,
    val dialogButton: IconButtonState,
    val onBack: () -> Unit
)
