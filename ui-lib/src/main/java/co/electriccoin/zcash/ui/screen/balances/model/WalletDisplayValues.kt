package co.electriccoin.zcash.ui.screen.balances.model

import android.content.Context
import androidx.compose.ui.text.intl.Locale
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.toFiatCurrencyState
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.extension.toKotlinLocale
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.totalBalance
import co.electriccoin.zcash.ui.common.viewmodel.STACKTRACE_LIMIT

data class WalletDisplayValues(
    val progress: PercentDecimal,
    val zecAmountText: String,
    val statusText: String,
    val statusAction: StatusAction = StatusAction.None,
    val fiatCurrencyAmountState: FiatCurrencyConversionRateState,
    val fiatCurrencyAmountText: String
) {
    companion object {
        @Suppress("LongMethod")
        internal fun getNextValues(
            context: Context,
            walletSnapshot: WalletSnapshot,
            isUpdateAvailable: Boolean = false,
        ): WalletDisplayValues {
            var progress = PercentDecimal.ZERO_PERCENT
            val zecAmountText = walletSnapshot.totalBalance().toZecString()
            var statusText = ""
            var statusAction: StatusAction = StatusAction.None

            val fiatCurrencyAmountState =
                walletSnapshot.spendableBalance().toFiatCurrencyState(
                    null,
                    Locale.current.toKotlinLocale(),
                    MonetarySeparators.current(java.util.Locale.getDefault())
                )
            var fiatCurrencyAmountText = getFiatCurrencyRateValue(context, fiatCurrencyAmountState)

            when (walletSnapshot.status) {
                Synchronizer.Status.SYNCING -> {
                    progress = walletSnapshot.progress
                    // We add "so far" to the amount
                    if (fiatCurrencyAmountState != FiatCurrencyConversionRateState.Unavailable) {
                        fiatCurrencyAmountText =
                            context.getString(
                                R.string.balances_status_syncing_amount_suffix,
                                fiatCurrencyAmountText
                            )
                    }
                    statusText = context.getString(R.string.balances_status_syncing)
                    statusAction = StatusAction.Syncing
                }
                Synchronizer.Status.SYNCED -> {
                    if (isUpdateAvailable) {
                        statusText =
                            context.getString(
                                R.string.balances_status_update,
                                context.getString(R.string.app_name)
                            )
                        statusAction = StatusAction.AppUpdate
                    } else {
                        statusText = context.getString(R.string.balances_status_synced)
                        statusAction = StatusAction.Synced
                    }
                }
                Synchronizer.Status.DISCONNECTED -> {
                    statusText =
                        context.getString(
                            R.string.balances_status_error_simple,
                            context.getString(R.string.app_name)
                        )
                    statusAction =
                        StatusAction.Disconnected(
                            details = context.getString(R.string.balances_status_error_dialog_connection)
                        )
                }
                Synchronizer.Status.STOPPED -> {
                    statusText = context.getString(R.string.balances_status_syncing)
                    statusAction =
                        StatusAction.Stopped(
                            details = context.getString(R.string.balances_status_dialog_stopped)
                        )
                }
            }

            // More detailed error message
            walletSnapshot.synchronizerError?.let {
                statusText =
                    context.getString(
                        R.string.balances_status_error_simple,
                        context.getString(R.string.app_name)
                    )
                statusAction =
                    StatusAction.Error(
                        details =
                            context.getString(
                                R.string.balances_status_error_dialog_cause,
                                walletSnapshot.synchronizerError.getCauseMessage()
                                    ?: context.getString(R.string.balances_status_error_dialog_cause_unknown),
                                walletSnapshot.synchronizerError.getStackTrace(limit = STACKTRACE_LIMIT)
                                    ?: context.getString(R.string.balances_status_error_dialog_stacktrace_unknown)
                            )
                    )
            }

            return WalletDisplayValues(
                progress = progress,
                zecAmountText = zecAmountText,
                statusAction = statusAction,
                statusText = statusText,
                fiatCurrencyAmountState = fiatCurrencyAmountState,
                fiatCurrencyAmountText = fiatCurrencyAmountText
            )
        }
    }
}

sealed class StatusAction {
    data object None : StatusAction()

    data object Syncing : StatusAction()

    data object Synced : StatusAction()

    data object AppUpdate : StatusAction()

    sealed class Detailed(open val details: String) : StatusAction()

    data class Disconnected(override val details: String) : Detailed(details)

    data class Stopped(override val details: String) : Detailed(details)

    data class Error(override val details: String) : Detailed(details)
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
