@file:Suppress("ktlint:filename")

package cash.z.ecc.sdk.ext

import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.FiatCurrencyConversionRateState

// TODO [#578] https://github.com/zcash/zcash-android-wallet-sdk/issues/578
fun Zatoshi.toFiatCurrencyState(): FiatCurrencyConversionRateState {
    return FiatCurrencyConversionRateState.Unavailable
    // this.convertZatoshiToZec()
    //    .convertZecToUsd(BigDecimal(100))
    //    .toUsdString()
}
