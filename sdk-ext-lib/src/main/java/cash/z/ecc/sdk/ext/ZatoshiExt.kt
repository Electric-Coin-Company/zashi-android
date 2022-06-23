package cash.z.ecc.sdk.ext

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import cash.z.ecc.android.sdk.ext.convertZecToUsd
import cash.z.ecc.android.sdk.ext.toUsdString
import cash.z.ecc.android.sdk.model.Zatoshi
import java.math.BigDecimal

// TODO [#578] https://github.com/zcash/zcash-android-wallet-sdk/issues/578
@Suppress("MagicNumber")
fun Zatoshi.toUsdString() =
    this.convertZatoshiToZec()
    .convertZecToUsd(BigDecimal(100))
    .toUsdString()
