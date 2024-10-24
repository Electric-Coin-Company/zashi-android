package co.electriccoin.zcash.ui.screen.request.ext

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import cash.z.ecc.android.sdk.model.FRACTION_DIGITS
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.toJavaLocale
import java.math.BigDecimal
import java.text.ParseException

internal fun String.convertToDouble(): Double? {
    val decimalFormat =
        DecimalFormat.getInstance(Locale.getDefault().toJavaLocale(), NumberFormat.NUMBERSTYLE).apply {
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
