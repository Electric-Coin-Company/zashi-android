package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

@Immutable
data class ExchangeRateTorState(
    val positive: ButtonState,
    val negative: ButtonState,
    override val onBack: () -> Unit
) : ModalBottomSheetState
