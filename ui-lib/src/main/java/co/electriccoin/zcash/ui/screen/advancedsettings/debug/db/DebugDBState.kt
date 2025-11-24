package co.electriccoin.zcash.ui.screen.advancedsettings.debug.db

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class DebugDBState(
    val query: TextFieldState,
    val output: StringResource,
    val execute: ButtonState,
    val onBack: () -> Unit,
)
