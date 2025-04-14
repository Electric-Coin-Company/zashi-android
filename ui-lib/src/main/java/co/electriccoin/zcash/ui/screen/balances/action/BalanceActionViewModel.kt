package co.electriccoin.zcash.ui.screen.balances.action

import androidx.lifecycle.ViewModel
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.loadingImageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow

class BalanceActionViewModel : ViewModel() {
    val state = MutableStateFlow(
        BalanceActionState(
            title = stringRes("Error"),
            message = stringRes("Something went wrong"),
            positive = ButtonState(
                text = stringRes("Positive")
            ),
            onBack = {},
            rows = listOf(
                BalanceActionRowState(
                    title = stringRes("Row"),
                    icon = loadingImageRes(),
                    value = stringRes("Value")
                ),
                BalanceActionRowState(
                    title = stringRes("Row"),
                    icon = imageRes(R.drawable.ic_home_buy),
                    value = stringRes("Value")
                )
            ),
            shieldButton = BalanceShieldButtonState(
                amount = Zatoshi(10000),
                onShieldClick = {}
            )
        )
    )
}