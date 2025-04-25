package co.electriccoin.zcash.ui.screen.balances

import androidx.compose.runtime.Immutable
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

@Immutable
data class BalanceWidgetState(
    val showDust: Boolean,
    val totalBalance: Zatoshi,
    val button: BalanceButtonState?,
    val exchangeRate: ExchangeRateState?,
)
