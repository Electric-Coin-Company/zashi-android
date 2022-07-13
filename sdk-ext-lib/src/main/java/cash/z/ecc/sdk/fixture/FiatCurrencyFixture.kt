package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.FiatCurrency

object FiatCurrencyFixture {
    const val USD = "USD"

    fun new(code: String = USD) = FiatCurrency(code)
}
