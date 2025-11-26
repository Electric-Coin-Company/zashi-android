package co.electriccoin.zcash.ui.common.wallet

import androidx.compose.runtime.Immutable
import cash.z.ecc.android.sdk.model.FiatCurrency
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion

@Immutable
sealed interface ExchangeRateState {
    @Immutable
    data class Data(
        val isLoading: Boolean = true,
        val isStale: Boolean = false,
        val isRefreshEnabled: Boolean = true,
        val currencyConversion: FiatCurrencyConversion? = null,
        val fiatCurrency: FiatCurrency = FiatCurrency.USD,
        val onRefresh: () -> Unit,
    ) : ExchangeRateState

    @Immutable
    data object OptIn : ExchangeRateState

    @Immutable
    data object OptedOut : ExchangeRateState
}
