package co.electriccoin.zcash.ui.screen.texunsupported

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class TEXUnsupportedState(
    override val onBack: () -> Unit,
) : ModalBottomSheetState
