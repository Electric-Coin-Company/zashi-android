package co.electriccoin.zcash.ui.screen.hotfix.ephemeral

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class EphemeralHotfixState(
    val address: TextFieldState,
    val button: ButtonState,
    val info: StringResource?,
    override val onBack: () -> Unit,
) : ModalBottomSheetState
