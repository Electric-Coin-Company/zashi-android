package co.electriccoin.zcash.ui.screen.swap.near

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.common.repository.SwapMode

@Immutable
data class NearInfoState(
    val mode: SwapMode,
    override val onBack: () -> Unit
): ModalBottomSheetState