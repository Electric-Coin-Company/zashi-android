@file:Suppress("ktlint:standard:filename")

package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

@Suppress("MagicNumber")
fun PercentDecimal.toPercentageWithDecimal(decimalFormat: DecimalFormat = preparePercentDecimalFormat()): String {
    return decimalFormat.format(decimal * 100)
}

private fun preparePercentDecimalFormat(): DecimalFormat =
    DecimalFormat().apply {
        val monetarySeparators = MonetarySeparators.current()
        val localizedPattern = "##0${monetarySeparators.decimal}00"
        runCatching {
            applyLocalizedPattern(localizedPattern)
        }.onFailure {
            Twig.error(it) { "Failed on applying localized pattern" }
        }
        roundingMode = RoundingMode.HALF_UP
    }
