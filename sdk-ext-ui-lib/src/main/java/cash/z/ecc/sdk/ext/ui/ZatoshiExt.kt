@file:Suppress("ktlint:filename")

package cash.z.ecc.sdk.ext.ui

import cash.z.ecc.android.sdk.ext.Conversions
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ext.ui.model.FiatCurrencyConversionRateState
import cash.z.ecc.sdk.ext.ui.model.Locale
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import cash.z.ecc.sdk.ext.ui.model.toJavaLocale
import cash.z.ecc.sdk.model.CurrencyConversion
import kotlinx.datetime.Clock
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import kotlin.time.Duration

fun Zatoshi.toFiatCurrencyState(
    currencyConversion: CurrencyConversion?,
    locale: Locale,
    monetarySeparators: MonetarySeparators,
    clock: Clock = Clock.System
): FiatCurrencyConversionRateState {
    if (currencyConversion == null) {
        return FiatCurrencyConversionRateState.Unavailable
    }

    val fiatCurrencyConversionRate = toFiatString(currencyConversion, locale, monetarySeparators)

    val currentSystemTime = clock.now()

    val age = currentSystemTime - currencyConversion.timestamp

    return if (age < Duration.ZERO && age.absoluteValue > FiatCurrencyConversionRateState.FUTURE_CUTOFF_AGE_INCLUSIVE) {
        // Special case if the device's clock is set to the future.
        // TODO [#535]: Consider using NTP requests to get the correct time instead of relying on the device's clock.
        FiatCurrencyConversionRateState.Unavailable
    } else if (age <= FiatCurrencyConversionRateState.CURRENT_CUTOFF_AGE_INCLUSIVE) {
        FiatCurrencyConversionRateState.Current(fiatCurrencyConversionRate)
    } else if (age <= FiatCurrencyConversionRateState.STALE_CUTOFF_AGE_INCLUSIVE) {
        FiatCurrencyConversionRateState.Stale(fiatCurrencyConversionRate)
    } else {
        FiatCurrencyConversionRateState.Unavailable
    }
}

fun Zatoshi.toFiatString(
    currencyConversion: CurrencyConversion,
    locale: Locale,
    monetarySeparators: MonetarySeparators
) =
    convertZatoshiToZecDecimal()
        .convertZecDecimalToFiatDecimal(BigDecimal(currencyConversion.priceOfZec))
        .convertFiatDecimalToFiatString(
            Currency.getInstance(currencyConversion.fiatCurrency.code),
            locale.toJavaLocale(),
            monetarySeparators
        )

private fun Zatoshi.convertZatoshiToZecDecimal(): BigDecimal {
    return BigDecimal(value, MathContext.DECIMAL128).divide(
        Conversions.ONE_ZEC_IN_ZATOSHI,
        MathContext.DECIMAL128
    ).setScale(Conversions.ZEC_FORMATTER.maximumFractionDigits, RoundingMode.HALF_EVEN)
}

private fun BigDecimal.convertZecDecimalToFiatDecimal(zecPrice: BigDecimal): BigDecimal {
    return multiply(zecPrice, MathContext.DECIMAL128)
}

private fun BigDecimal.convertFiatDecimalToFiatString(
    fiatCurrency: Currency,
    locale: java.util.Locale,
    monetarySeparators: MonetarySeparators
): String {
    return NumberFormat.getCurrencyInstance(locale).apply {
        currency = fiatCurrency
        roundingMode = RoundingMode.HALF_EVEN
        if (this is DecimalFormat) {
            decimalFormatSymbols.apply {
                decimalSeparator = monetarySeparators.decimal
                monetaryDecimalSeparator = monetarySeparators.decimal
                groupingSeparator = monetarySeparators.grouping
            }
        }
    }.format(this)
}
