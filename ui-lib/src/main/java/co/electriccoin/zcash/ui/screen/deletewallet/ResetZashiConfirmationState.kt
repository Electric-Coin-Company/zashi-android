package co.electriccoin.zcash.ui.screen.deletewallet

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class ResetZashiConfirmationState(
    override val onBack: () -> Unit,
    val onConfirm: () -> Unit,
    val onCancel: () -> Unit
) : ModalBottomSheetState
