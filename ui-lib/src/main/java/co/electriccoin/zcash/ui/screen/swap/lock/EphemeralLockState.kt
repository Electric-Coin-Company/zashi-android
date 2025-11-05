package co.electriccoin.zcash.ui.screen.swap.lock

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteInfoItem

@Immutable
internal data class EphemeralLockState(
    val items: List<SwapQuoteInfoItem>,
    val amount: SwapQuoteInfoItem,
    val secondaryButton: ButtonState,
    val primaryButton: ButtonState,
    override val onBack: () -> Unit,
) : ModalBottomSheetState
