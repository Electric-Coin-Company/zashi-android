package co.electriccoin.zcash.ui.screen.request.ext

import android.content.Context
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import cash.z.ecc.android.sdk.model.FRACTION_DIGITS
import co.electriccoin.zcash.ui.design.util.getPreferredLocale
import java.text.ParseException

internal fun String.convertToDouble(context: Context): Double? {
    val decimalFormat =
        DecimalFormat
            .getInstance(
                context.resources.configuration.getPreferredLocale(),
                NumberFormat.NUMBERSTYLE
            ).apply {
                roundingMode = android.icu.math.BigDecimal.ROUND_HALF_EVEN // aka Bankers rounding
                maximumFractionDigits = FRACTION_DIGITS
                minimumFractionDigits = FRACTION_DIGITS
            }

    return try {
        decimalFormat.parse(this).toDouble()
    } catch (e: ParseException) {
        null
    }
}
