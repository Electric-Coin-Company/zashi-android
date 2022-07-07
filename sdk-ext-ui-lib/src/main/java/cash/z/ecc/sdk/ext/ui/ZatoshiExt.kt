@file:Suppress("ktlint:filename")

package cash.z.ecc.sdk.ext.ui

import cash.z.ecc.android.sdk.ext.Conversions
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import cash.z.ecc.sdk.model.CurrencyConversion
import cash.z.ecc.sdk.model.FiatCurrencyConversionRateState
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Zatoshi.toFiatCurrencyState(
    currencyConversion: CurrencyConversion?,
    monetarySeparators: MonetarySeparators
): FiatCurrencyConversionRateState {
    if (currencyConversion == null) {
        return FiatCurrencyConversionRateState.Unavailable
    }

    val fiatCurrencyConversionRate = toFiatString(currencyConversion, monetarySeparators)

    return if (currencyConversion.timestamp.toEpochMilliseconds() <= CurrencyConversion.STALENESS_PERIOD) {
        FiatCurrencyConversionRateState.Current(fiatCurrencyConversionRate)
    } else {
        FiatCurrencyConversionRateState.Stale(fiatCurrencyConversionRate)
    }
}

@Suppress("MagicNumber")
fun Zatoshi.toFiatString(currencyConversion: CurrencyConversion, monetarySeparators: MonetarySeparators) =
    convertZatoshiToZecDecimal()
        .convertZecDecimalToFiatDecimal(BigDecimal(currencyConversion.priceOfZec))
        .convertFiatDecimalToFiatString(monetarySeparators)

private fun Zatoshi.convertZatoshiToZecDecimal(): BigDecimal {
    return BigDecimal(value, MathContext.DECIMAL128).divide(
        Conversions.ONE_ZEC_IN_ZATOSHI,
        MathContext.DECIMAL128
    ).setScale(Conversions.ZEC_FORMATTER.maximumFractionDigits, RoundingMode.HALF_EVEN)
}

private fun BigDecimal.convertZecDecimalToFiatDecimal(zecPrice: BigDecimal): BigDecimal {
    return multiply(zecPrice, MathContext.DECIMAL128)
}

private fun BigDecimal?.convertFiatDecimalToFiatString(monetarySeparators: MonetarySeparators): String {
    val decimalFormat = DecimalFormat().apply {
        isParseBigDecimal = true
        roundingMode = RoundingMode.HALF_EVEN
        maximumFractionDigits = Conversions.USD_FORMATTER.maximumFractionDigits
        minimumFractionDigits = Conversions.USD_FORMATTER.minimumFractionDigits
        decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US).apply {
            this.groupingSeparator = monetarySeparators.grouping
            this.decimalSeparator = monetarySeparators.decimal
        }
    }

    return decimalFormat.format(this)
}
