package cash.z.ecc.sdk.model

/**
 * Represents the state of current fiat currency conversion to ZECs.
 */
sealed class FiatCurrencyConversionRateState {
    class Current(val value: String) : FiatCurrencyConversionRateState()
    class Stale(val value: String) : FiatCurrencyConversionRateState()
    object Unavailable : FiatCurrencyConversionRateState()
}
