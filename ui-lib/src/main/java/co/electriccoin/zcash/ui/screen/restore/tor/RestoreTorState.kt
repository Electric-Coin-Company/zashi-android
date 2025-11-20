package co.electriccoin.zcash.ui.screen.restore.tor

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class RestoreTorState(
    val checkbox: CheckboxState,
    val primary: ButtonState,
    val secondary: ButtonState,
    override val onBack: () -> Unit
) : ModalBottomSheetState
