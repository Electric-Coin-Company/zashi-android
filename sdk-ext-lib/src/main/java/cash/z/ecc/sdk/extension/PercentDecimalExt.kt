@file:Suppress("ktlint:filename")

package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

@Suppress("MagicNumber")
fun PercentDecimal.toPercentageWithDecimal(decimalFormat: DecimalFormat = preparePercentDecimalFormat()): String {
    return decimalFormat.format(decimal * 100)
}

private fun preparePercentDecimalFormat(): DecimalFormat = DecimalFormat().apply {
    val monetarySeparators = MonetarySeparators.current()
    val localizedPattern = "##0${monetarySeparators.decimal}00"
    applyLocalizedPattern(localizedPattern)
    roundingMode = RoundingMode.HALF_UP
}
