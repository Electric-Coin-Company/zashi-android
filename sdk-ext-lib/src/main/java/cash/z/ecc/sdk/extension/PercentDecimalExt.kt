@file:Suppress("ktlint:standard:filename")

package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale

@Suppress("MagicNumber")
fun PercentDecimal.toPercentageWithDecimal(decimalFormat: DecimalFormat = preparePercentDecimalFormat()): String =
    decimalFormat.format(decimal * 100)

private fun preparePercentDecimalFormat(): DecimalFormat =
    DecimalFormat().apply {
        val monetarySeparators = MonetarySeparators.current(Locale.getDefault())
        val localizedPattern = "##0${monetarySeparators.decimal}00"
        runCatching {
            applyLocalizedPattern(localizedPattern)
        }.onFailure {
            Twig.error(it) { "Failed on applying localized pattern" }
        }
        roundingMode = RoundingMode.HALF_UP
    }
