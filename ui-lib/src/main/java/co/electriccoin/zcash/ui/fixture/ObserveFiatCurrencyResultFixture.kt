package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.datetime.Clock

object ObserveFiatCurrencyResultFixture {
    const val IS_LOADING: Boolean = true
    const val IS_STALE: Boolean = false
    const val IS_REFRESH_ENABLED: Boolean = false
    val CURRENCY_CONVERSION: FiatCurrencyConversion = FiatCurrencyConversion(
        timestamp = Clock.System.now(),
        priceOfZec = 25.0
    )

    fun new(
        isLoading: Boolean = IS_LOADING,
        isStale: Boolean = IS_STALE,
        isRefreshEnabled: Boolean = IS_REFRESH_ENABLED,
        currencyConversion: FiatCurrencyConversion? = CURRENCY_CONVERSION,
    ) = ExchangeRateState.Data(isLoading, isStale, isRefreshEnabled, currencyConversion) {}
}
