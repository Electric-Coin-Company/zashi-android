package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.FiatCurrencyResult
import kotlinx.datetime.Clock

object FiatCurrencyResultFixture {
    fun new() =
        FiatCurrencyResult.Success(
            FiatCurrencyConversion(
                fiatCurrency = FiatCurrency.USD,
                timestamp = Clock.System.now(),
                priceOfZec = 25.0
            )
        )
}
