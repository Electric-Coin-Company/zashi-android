package co.electriccoin.zcash.ui.screen.hotfix.ephemeral

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.TextFieldState

@Immutable
data class EphemeralHotfixState(
    val address: TextFieldState,
    val button: ButtonState,
    override val onBack: () -> Unit,
): ModalBottomSheetState
