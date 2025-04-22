package co.electriccoin.zcash.ui.screen.balances.action

import androidx.compose.runtime.Immutable
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class BalanceActionState(
    val title: StringResource,
    val message: StringResource,
    val rows: List<BalanceActionRowState>,
    val shieldButton: BalanceShieldButtonState?,
    val positive: ButtonState,
    override val onBack: () -> Unit,
) : ModalBottomSheetState

@Immutable
data class BalanceActionRowState(
    val title: StringResource,
    val icon: ImageResource,
    val value: StringResource
)

@Immutable
data class BalanceShieldButtonState(
    val amount: Zatoshi,
    val onShieldClick: () -> Unit,
)
