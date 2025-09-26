package co.electriccoin.zcash.ui.screen.pay

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ChipButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.screen.swap.SwapErrorFooterState

@Immutable
internal data class PayState(
    val info: IconButtonState,
    val address: TextFieldState,
    val addressPlaceholder: StringResource,
    val asset: AssetCardState,
    val abContact: ChipButtonState? = null,
    val abButton: IconButtonState,
    val qrButton: IconButtonState,
    val amount: NumberTextFieldState,
    val amountFiat: NumberTextFieldState,
    val amountError: StringResource? = null,
    val zecAmount: StyledStringResource,
    val slippage: ButtonState,
    val errorFooter: SwapErrorFooterState?,
    val primaryButton: ButtonState?,
    val isABHintVisible: Boolean,
    val onBack: () -> Unit,
)
