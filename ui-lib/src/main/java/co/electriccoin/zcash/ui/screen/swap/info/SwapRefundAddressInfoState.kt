package co.electriccoin.zcash.ui.screen.swap.info

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class SwapRefundAddressInfoState(
    val message: StringResource,
    override val onBack: () -> Unit
) : ModalBottomSheetState
