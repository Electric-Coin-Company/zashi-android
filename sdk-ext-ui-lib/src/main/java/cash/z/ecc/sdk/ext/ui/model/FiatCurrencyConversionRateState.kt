package cash.z.ecc.sdk.ext.ui.model

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

/**
 * Represents a state of current fiat currency conversion to ZECs.
 */
sealed class FiatCurrencyConversionRateState {
    /**
     * @param formattedFiatValue A fiat value formatted as a localized string.  E.g. $1.00.
     */
    data class Current(val formattedFiatValue: String) : FiatCurrencyConversionRateState()

    /**
     * @param formattedFiatValue A fiat value formatted as a localized string.  E.g. $1.00.
     */
    data class Stale(val formattedFiatValue: String) : FiatCurrencyConversionRateState()
    object Unavailable : FiatCurrencyConversionRateState()

    companion object {

        /**
         * Cutoff negative age.  Some users may intentionally set their clock forward 10 minutes
         * because they're always late to things.  This allows the app to mostly work for those users,
         * while still failing if the clock is way off.
         */
        val FUTURE_CUTOFF_AGE_INCLUSIVE = 10.minutes

        /**
         * Cutoff age for next attempt to refresh the conversion rate from the API.
         */
        val CURRENT_CUTOFF_AGE_INCLUSIVE = 1.minutes

        /**
         * Cutoff age for displaying conversion rate from prior app launches or background refresh.
         */
        val STALE_CUTOFF_AGE_INCLUSIVE = 1.days
    }
}
