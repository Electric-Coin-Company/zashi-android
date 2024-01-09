@file:Suppress("ktlint:standard:filename")

package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

@Suppress("MagicNumber")
fun PercentDecimal.toPercentageWithDecimal(decimalFormat: DecimalFormat = preparePercentDecimalFormat()): String {
    return decimalFormat.format(decimal * 100)
}

private fun preparePercentDecimalFormat(): DecimalFormat =
    DecimalFormat().apply {
        // TODO [#1171]: Remove default MonetarySeparators locale
        // TODO [#1171]: https://github.com/Electric-Coin-Company/zashi-android/issues/1171
        val monetarySeparators = MonetarySeparators.current(Locale.US)
        val localizedPattern = "##0${monetarySeparators.decimal}00"
        runCatching {
            applyLocalizedPattern(localizedPattern)
        }.onFailure {
            Twig.error(it) { "Failed on applying localized pattern" }
        }
        roundingMode = RoundingMode.HALF_UP
    }
