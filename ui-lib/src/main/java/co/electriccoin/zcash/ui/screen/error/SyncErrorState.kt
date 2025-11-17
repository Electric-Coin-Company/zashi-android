package co.electriccoin.zcash.ui.screen.error

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class SyncErrorState(
    val switchServer: ButtonState,
    val disableTor: ButtonState?,
    val support: ButtonState,
    override val onBack: () -> Unit
) : ModalBottomSheetState
