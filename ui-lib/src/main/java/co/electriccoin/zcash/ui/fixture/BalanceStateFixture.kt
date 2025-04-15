package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetState

object BalanceStateFixture {
    private const val BALANCE_VALUE = 0L

    val TOTAL_BALANCE = Zatoshi(BALANCE_VALUE)

    fun new(
        totalBalance: Zatoshi = TOTAL_BALANCE,
        exchangeRate: ExchangeRateState = ObserveFiatCurrencyResultFixture.new()
    ) = BalanceWidgetState(
        totalBalance = totalBalance,
        exchangeRate = exchangeRate,
        button = null,
        showDust = true
    )
}
