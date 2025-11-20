package co.electriccoin.zcash.ui.screen.insufficientfunds

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class InsufficientFundsState(
    override val onBack: () -> Unit,
) : ModalBottomSheetState
