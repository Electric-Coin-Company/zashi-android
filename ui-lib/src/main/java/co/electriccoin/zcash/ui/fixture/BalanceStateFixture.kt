package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.FiatCurrencyResult
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.compose.BalanceState

object BalanceStateFixture {
    private const val BALANCE_VALUE = 0L

    val TOTAL_BALANCE = Zatoshi(BALANCE_VALUE)
    val SPENDABLE_BALANCE = Zatoshi(BALANCE_VALUE)

    fun new(
        totalBalance: Zatoshi = TOTAL_BALANCE,
        spendableBalance: Zatoshi = SPENDABLE_BALANCE,
        exchangeRate: FiatCurrencyResult = FiatCurrencyResultFixture.new()
    ) = BalanceState.Available(
        totalBalance = totalBalance,
        spendableBalance = spendableBalance,
        exchangeRate = exchangeRate
    )
}
