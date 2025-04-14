package co.electriccoin.zcash.ui.screen.balances

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class BalanceState(
    val totalBalance: Zatoshi,
    val button: BalanceButtonState?,
    val exchangeRate: ExchangeRateState?,
)

@Immutable
data class BalanceButtonState(
    @DrawableRes val icon: Int,
    val text: StringResource,
    val amount: Zatoshi?,
    val onClick: () -> Unit
)
