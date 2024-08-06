package co.electriccoin.zcash.ui.common.wallet

import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion

data class ExchangeRateState(
    val isLoading: Boolean = true,
    val isStale: Boolean = false,
    val isRefreshEnabled: Boolean = true,
    val currencyConversion: FiatCurrencyConversion? = null,
    val onRefresh: () -> Unit
) {
    val fiatCurrency: FiatCurrency
        get() = FiatCurrency.USD
}
