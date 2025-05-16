package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.Zatoshi
import kotlin.math.floor

private const val DECIMALS_MAX_LONG = 8
private const val DECIMALS_MIN_LONG = 3

private const val DECIMALS_SHORT = 3

private const val MIN_ZATOSHI_FOR_DOTS_SHORT = Zatoshi.ZATOSHI_PER_ZEC / 1000

val Zatoshi.Companion.ZERO: Zatoshi
    get() = Zatoshi(0)

fun Zatoshi.toZecStringFull() =
    convertZatoshiToZecString(
        maxDecimals = DECIMALS_MAX_LONG,
        minDecimals = DECIMALS_MIN_LONG
    )

fun Zatoshi.toZecStringAbbreviated(suffix: String): ZecAmountPair {
    val checkedSuffix =
        if (value in 1..<MIN_ZATOSHI_FOR_DOTS_SHORT) {
            suffix
        } else {
            ""
        }
    return convertZatoshiToZecString(
        minDecimals = DECIMALS_SHORT,
        maxDecimals = DECIMALS_SHORT
    ).let { mainPart ->
        ZecAmountPair(
            main = mainPart,
            suffix = checkedSuffix
        )
    }
}

@Suppress("MagicNumber")
fun Zatoshi.floor(): Zatoshi = Zatoshi(floorRoundBy(value.toDouble(), 5000.0).toLong())

data class ZecAmountPair(
    val main: String,
    val suffix: String
)

val Zatoshi.Companion.typicalFee: Zatoshi
    get() = Zatoshi(TYPICAL_FEE)

private const val TYPICAL_FEE = 100000L

private fun floorRoundBy(number: Double, multiple: Double): Double {
    require(multiple != 0.0) { "Multiple cannot be zero" }
    return floor(number / multiple) * multiple
}
