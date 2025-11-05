package co.electriccoin.zcash.ui.screen.advancedsettings.debug.text

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class DebugTextState(
    val title: StringResource,
    val text: StringResource,
    override val onBack: () -> Unit
): ModalBottomSheetState