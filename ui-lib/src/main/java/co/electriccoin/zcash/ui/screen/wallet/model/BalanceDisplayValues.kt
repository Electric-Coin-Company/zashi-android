package co.electriccoin.zcash.ui.screen.wallet.model

import android.content.Context
import androidx.annotation.DrawableRes
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot

data class BalanceDisplayValues(
    @DrawableRes val iconDrawableRes: Int,
    val balance: String,
    val balanceUnit: String,
    val balanceType: String,
    val msg: String?
) {
    companion object {
        internal fun getNextValue(context: Context, balanceViewType: BalanceViewType, walletSnapshot: WalletSnapshot): BalanceDisplayValues {
            var iconDrawableRes = R.drawable.ic_icon_left_swipe
            var balance = ""
            val balanceUnit = context.getString(R.string.ns_zec)
            var balanceType = ""
            var msg: String? = null

            when (balanceViewType) {
                BalanceViewType.SWIPE -> {
                    iconDrawableRes = R.drawable.ic_icon_left_swipe
                    msg = context.getString(R.string.ns_swipe_left)
                }
                BalanceViewType.TOTAL -> {
                    iconDrawableRes = R.drawable.ic_icon_total
                    balance = walletSnapshot.saplingBalance.total.plus(walletSnapshot.transparentBalance.total).toZecString()
                    balanceType = context.getString(R.string.ns_total_balance)
                }
                BalanceViewType.SHIELDED -> {
                    iconDrawableRes = R.drawable.ic_icon_shielded
                    balance = walletSnapshot.saplingBalance.total.toZecString()
                    balanceType = context.getString(R.string.ns_shielded_balance)
                }
                BalanceViewType.TRANSPARENT -> {
                    iconDrawableRes = R.drawable.ic_icon_transparent
                    balance = walletSnapshot.transparentBalance.total.toZecString()
                    balanceType = context.getString(R.string.ns_transparent_balance)
                }
            }

            return BalanceDisplayValues(iconDrawableRes, balance, balanceUnit, balanceType, msg)
        }
    }
}