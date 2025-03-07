package co.electriccoin.zcash.ui.screen.balances

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

sealed interface BalanceState {
    val totalBalance: Zatoshi
    val spendableBalance: Zatoshi
    val exchangeRate: ExchangeRateState

    data class None(
        override val exchangeRate: ExchangeRateState
    ) : BalanceState {
        override val totalBalance: Zatoshi = Zatoshi(0L)
        override val spendableBalance: Zatoshi = Zatoshi(0L)
    }

    data class Loading(
        override val totalBalance: Zatoshi,
        override val spendableBalance: Zatoshi,
        override val exchangeRate: ExchangeRateState,
    ) : BalanceState

    data class Available(
        override val totalBalance: Zatoshi,
        override val spendableBalance: Zatoshi,
        override val exchangeRate: ExchangeRateState,
    ) : BalanceState
}
