package co.electriccoin.zcash.ui.screen.home.balance

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

data class TransparentBalanceInfoState(
    val transparentAmount: Zatoshi,
    override val onBack: () -> Unit,
    val primaryButton: ButtonState,
    val secondaryButton: ButtonState
) : ModalBottomSheetState
