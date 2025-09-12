package co.electriccoin.zcash.ui.screen.swap.onrampquote

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class ORQuoteState(
    val info: IconButtonState,
    val bigIcon: ImageResource,
    val smallIcon: ImageResource,
    val amount: StringResource,
    val amountFiat: StringResource,
    val onAmountClick: () -> Unit,
    val qr: String,
    val copyButton: BigIconButtonState,
    val shareButton: BigIconButtonState,
    val footer: StringResource,
    val primaryButton: ButtonState,
    val onBack: () -> Unit
)
