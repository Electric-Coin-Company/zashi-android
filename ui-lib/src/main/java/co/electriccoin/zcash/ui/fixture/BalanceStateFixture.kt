package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

object BalanceStateFixture {
    private const val BALANCE_VALUE = 0L

    val TOTAL_BALANCE = Zatoshi(BALANCE_VALUE)
    val TOTAL_SHIELDED_BALANCE = Zatoshi(BALANCE_VALUE)
    val SPENDABLE_BALANCE = Zatoshi(BALANCE_VALUE)

    fun new(
        totalBalance: Zatoshi = TOTAL_BALANCE,
        totalShieldedBalance: Zatoshi = TOTAL_SHIELDED_BALANCE,
        spendableBalance: Zatoshi = SPENDABLE_BALANCE,
        exchangeRate: ExchangeRateState = ObserveFiatCurrencyResultFixture.new()
    ) = BalanceState.Available(
        totalBalance = totalBalance,
        spendableBalance = spendableBalance,
        exchangeRate = exchangeRate,
        totalShieldedBalance = totalShieldedBalance
    )
}
