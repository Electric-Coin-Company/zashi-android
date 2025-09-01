package co.electriccoin.zcash.ui.screen.pay.info

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class PayInfoState(
    override val onBack: () -> Unit
) : ModalBottomSheetState
