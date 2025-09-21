package co.electriccoin.zcash.ui.screen.swap.orconfirmation

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.QrCodeColors
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StyledStringResource

@Immutable
data class ORSwapConfirmationState(
    val info: IconButtonState,
    val bigIcon: ImageResource,
    val smallIcon: ImageResource,
    val amount: StringResource,
    val amountFiat: StringResource,
    val onAmountClick: () -> Unit,
    val qr: String,
    val address: StringResource,
    val copyButton: BigIconButtonState,
    val shareButton: BigIconButtonState,
    val footer: StyledStringResource,
    val primaryButton: ButtonState,
    val onBack: () -> Unit
)
