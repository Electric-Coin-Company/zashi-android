package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.datetime.Clock

object ObserveFiatCurrencyResultFixture {
    fun new(
        isLoading: Boolean = true,
        isStale: Boolean = false,
        isRefreshEnabled: Boolean = true,
        currencyConversion: FiatCurrencyConversion? =
            FiatCurrencyConversion(
                timestamp = Clock.System.now(),
                priceOfZec = 25.0
            ),
    ) = ExchangeRateState.Data(isLoading, isStale, isRefreshEnabled, currencyConversion) {}
}
