package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.ext.convertZecToUsd
import cash.z.ecc.android.sdk.ext.toUsdString
import java.math.BigDecimal

/**
 *
 */
@JvmInline
value class Zatoshi(val value: Long) {
    init {
        require(value >= 0)
    }

    companion object
}

// TODO [#578] https://github.com/zcash/zcash-android-wallet-sdk/issues/578
@Suppress("MagicNumber")
fun Zatoshi.toUsdString() = value
    .convertZatoshiToZec()
    .convertZecToUsd(BigDecimal(100))
    .toUsdString()
