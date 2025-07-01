package co.electriccoin.zcash.ui.screen.swap.info

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class SwapInfoState(
    val mode: SwapMode,
    override val onBack: () -> Unit
) : ModalBottomSheetState
