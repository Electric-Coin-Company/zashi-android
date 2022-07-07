package cash.z.ecc.sdk.model

import kotlinx.datetime.Instant

/**
 * Represents a state of current fiat currency conversion to ZECs.
 */
sealed class FiatCurrencyConversionRateState {
    class Current(val value: String) : FiatCurrencyConversionRateState()
    class Stale(val value: String) : FiatCurrencyConversionRateState()
    object Unavailable : FiatCurrencyConversionRateState()
}

/**
 * Represents a snapshot in time of a currency conversion rate.
 *
 * @param fiatCurrency The fiat currency for this conversion.
 * @param timestamp The timestamp when this conversion was obtained. This value is returned by
 * the server so it shouldn't have issues with client-side clock inaccuracy.
 * @param priceOfZec The conversion rate of ZEC to the fiat currency.
 */
data class CurrencyConversion(
    val fiatCurrency: FiatCurrency,
    val timestamp: Instant,
    val priceOfZec: Double
) {
    init {
        require(priceOfZec > 0) { "priceOfZec must be greater than 0" }
        require(priceOfZec.isFinite()) { "priceOfZec must be finite" }
    }

    companion object {
        const val STALENESS_PERIOD = 3600000 // 1 hours
    }
}

/**
 * Represents an ISO 4217 currency code.
 */
@Suppress("MagicNumber")
data class FiatCurrency(val code: String) {
    init {
        require(code.length == 3) { "Fiat currency code must be 3 characters long." }

        // TODO [#532] https://github.com/zcash/secant-android-wallet/issues/532
        // Add another check to make sure the code is in the known ISO currency code list.
    }
}
