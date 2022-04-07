package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.ext.Conversions
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Locale

object ZecString {

    fun allowedCharacters(monetarySeparators: MonetarySeparators) = buildSet<Char> {
        add('0')
        add('1')
        add('2')
        add('3')
        add('4')
        add('5')
        add('6')
        add('7')
        add('8')
        add('9')
        add(monetarySeparators.decimal)
        add(monetarySeparators.grouping)
    }
}

data class MonetarySeparators(val grouping: Char, val decimal: Char) {
    init {
        require(grouping != decimal) { "Grouping and decimal separator cannot be the same character" }
    }

    companion object {
        /**
         * @return The current localized monetary separators.  Do not cache this value, as it
         * can change if the system Locale changes.
         */
        fun current(): MonetarySeparators {
            val decimalFormatSymbols = DecimalFormatSymbols.getInstance()

            return MonetarySeparators(
                decimalFormatSymbols.groupingSeparator,
                decimalFormatSymbols.monetaryDecimalSeparator
            )
        }
    }
}

private const val DECIMALS = 8

// TODO [#412]: https://github.com/zcash/zcash-android-wallet-sdk/issues/412
// The SDK needs to fix the API for currency conversion
fun Zatoshi.toZecString() = value.convertZatoshiToZecString(DECIMALS, DECIMALS)

/*
 * ZEC is our own currency, so there's not going to be an existing localization that matches it perfectly.
 *
 * To ensure consistent behavior regardless of user Locale, use US localization except that we swap out the
 * separator characters based on the user's current Locale.  This should avoid unexpected surprises
 * while also localizing the separator format.
 */
/**
 * @return [zecString] parsed into Zatoshi or null if parsing failed.
 */
@SuppressWarnings("ReturnCount")
fun Zatoshi.Companion.fromZecString(zecString: String, monetarySeparators: MonetarySeparators): Zatoshi? {
    if (zecString.isBlank()) {
        return null
    }

    val symbols = DecimalFormatSymbols.getInstance(Locale.US).apply {
        this.groupingSeparator = monetarySeparators.grouping
        this.decimalSeparator = monetarySeparators.decimal
    }
    val localizedPattern = "#${monetarySeparators.grouping}##0${monetarySeparators.decimal}0#"

    // TODO [#321]: https://github.com/zcash/secant-android-wallet/issues/321
    val decimalFormat = DecimalFormat(localizedPattern, symbols).apply {
        isParseBigDecimal = true
        roundingMode = RoundingMode.HALF_EVEN // aka Bankers rounding
    }

    val bigDecimal = try {
        decimalFormat.parse(zecString) as BigDecimal
    } catch (e: NumberFormatException) {
        null
    } catch (e: ParseException) {
        null
    }

    // TODO [472]: https://github.com/zcash/zcash-android-wallet-sdk/issues/472
    // temporary workaround to prevent SDK to returns us negative result
    if (bigDecimal?.times(Conversions.ONE_ZEC_IN_ZATOSHI)?.compareTo(BigDecimal(Long.MAX_VALUE)) ?: 0 > 0) {
        return null
    }

    return Zatoshi(bigDecimal.convertZecToZatoshi())
}
