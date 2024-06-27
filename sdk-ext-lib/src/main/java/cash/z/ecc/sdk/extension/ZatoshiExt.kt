package cash.z.ecc.sdk.extension

import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.Zatoshi

private const val DECIMALS_MAX_LONG = 8
private const val DECIMALS_MIN_LONG = 3

private const val DECIMALS_SHORT = 3

private const val MIN_ZATOSHI_FOR_DOTS_SHORT = Zatoshi.ZATOSHI_PER_ZEC / 1000

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

private const val DEFAULT_LESS_THAN_FEE = 100_000L

val DEFAULT_FEE: String
    get() = Zatoshi(DEFAULT_LESS_THAN_FEE).toZecStringFull()

data class ZecAmountPair(
    val main: String,
    val suffix: String
)
