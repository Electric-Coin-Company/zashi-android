package co.electriccoin.zcash.ui.screen.home.model

import android.content.Context
import androidx.compose.ui.text.intl.Locale
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.toFiatCurrencyState
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.toKotlinLocale
import kotlin.math.roundToInt

data class WalletDisplayValues(
    val progress: PercentDecimal,
    val zecAmountText: String,
    val statusText: String,
    val fiatCurrencyAmountState: FiatCurrencyConversionRateState,
    val fiatCurrencyAmountText: String
) {
    companion object {
        @Suppress("MagicNumber", "LongMethod")
        internal fun getNextValues(
            context: Context,
            walletSnapshot: WalletSnapshot,
            updateAvailable: Boolean
        ): WalletDisplayValues {
            var progress = PercentDecimal.ZERO_PERCENT
            val zecAmountText = walletSnapshot.totalBalance().toZecString()
            var statusText = ""
            // TODO [#578] https://github.com/zcash/zcash-android-wallet-sdk/issues/578
            // We'll ideally provide a "fresh" currencyConversion object here
            val fiatCurrencyAmountState = walletSnapshot.spendableBalance().toFiatCurrencyState(
                null,
                Locale.current.toKotlinLocale(),
                MonetarySeparators.current()
            )
            var fiatCurrencyAmountText = getFiatCurrencyRateValue(context, fiatCurrencyAmountState)

            when (walletSnapshot.status) {
                Synchronizer.Status.PREPARING,
                Synchronizer.Status.DOWNLOADING,
                Synchronizer.Status.VALIDATING -> {
                    progress = walletSnapshot.progress
                    val progressPercent = (walletSnapshot.progress.decimal * 100).roundToInt()
                    // we add "so far" to the amount
                    if (fiatCurrencyAmountState != FiatCurrencyConversionRateState.Unavailable) {
                        fiatCurrencyAmountText = context.getString(
                            R.string.home_status_syncing_amount_suffix,
                            fiatCurrencyAmountText
                        )
                    }
                    statusText = context.getString(R.string.home_status_syncing_format, progressPercent)
                }
                Synchronizer.Status.SCANNING -> {
                    // SDK provides us only one progress, which keeps on 100 in the scanning state
                    progress = PercentDecimal.ONE_HUNDRED_PERCENT
                    statusText = context.getString(R.string.home_status_syncing_catchup)
                }
                Synchronizer.Status.SYNCED,
                Synchronizer.Status.ENHANCING -> {
                    statusText = if (updateAvailable) {
                        context.getString(R.string.home_status_update)
                    } else {
                        context.getString(R.string.home_status_up_to_date)
                    }
                }
                Synchronizer.Status.DISCONNECTED -> {
                    statusText = context.getString(
                        R.string.home_status_error,
                        context.getString(R.string.home_status_error_connection)
                    )
                }
                Synchronizer.Status.STOPPED -> {
                    statusText = context.getString(R.string.home_status_stopped)
                }
            }

            // more detailed error message
            walletSnapshot.synchronizerError?.let {
                statusText = context.getString(
                    R.string.home_status_error,
                    walletSnapshot.synchronizerError.getCauseMessage()
                        ?: context.getString(R.string.home_status_error_unknown)
                )
            }

            return WalletDisplayValues(
                progress = progress,
                zecAmountText = zecAmountText,
                statusText = statusText,
                fiatCurrencyAmountState = fiatCurrencyAmountState,
                fiatCurrencyAmountText = fiatCurrencyAmountText
            )
        }
    }
}

private fun getFiatCurrencyRateValue(
    context: Context,
    fiatCurrencyAmountState: FiatCurrencyConversionRateState
): String {
    return fiatCurrencyAmountState.let { state ->
        when (state) {
            is FiatCurrencyConversionRateState.Current -> state.formattedFiatValue
            is FiatCurrencyConversionRateState.Stale -> state.formattedFiatValue
            is FiatCurrencyConversionRateState.Unavailable -> {
                context.getString(R.string.fiat_currency_conversion_rate_unavailable)
            }
        }
    }
}
