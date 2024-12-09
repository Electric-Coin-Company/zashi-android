package co.electriccoin.zcash.ui.common.model

import androidx.annotation.DrawableRes
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.Zip32AccountIndex
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes

sealed interface WalletAccount {

    val sdkAccount: Account

    val unifiedAddress: WalletAddress.Unified
    val saplingAddress: WalletAddress.Sapling
    val transparentAddress: WalletAddress.Transparent
    val saplingBalance: WalletBalance
    val orchardBalance: WalletBalance
    val transparentBalance: Zatoshi
    val isSelected: Boolean
    val name: StringResource
    @get:DrawableRes val icon: Int

    val hdAccountIndex: Zip32AccountIndex
        get() = sdkAccount.hdAccountIndex!!

    val totalBalance: Zatoshi
        get() = orchardBalance.total + saplingBalance.total + transparentBalance
    val totalShieldedBalance: Zatoshi
        get() = orchardBalance.total + saplingBalance.total
    val spendableBalance: Zatoshi
        get() = orchardBalance.available + saplingBalance.available
    val changePendingBalance: Zatoshi
        get() = orchardBalance.changePending + saplingBalance.changePending
    val hasChangePending: Boolean
        get() = changePendingBalance.value > 0L
    val valuePendingBalance: Zatoshi
        get() = orchardBalance.valuePending + saplingBalance.valuePending
    val hasValuePending: Boolean
        get() = valuePendingBalance.value > 0L
    
    fun canSpend(amount: Zatoshi): Boolean = spendableBalance >= amount
}

data class ZashiAccount(
    override val sdkAccount: Account,
    override val unifiedAddress: WalletAddress.Unified,
    override val saplingAddress: WalletAddress.Sapling,
    override val transparentAddress: WalletAddress.Transparent,
    override val saplingBalance: WalletBalance,
    override val orchardBalance: WalletBalance,
    override val transparentBalance: Zatoshi,
    override val isSelected: Boolean,
    override val name: StringResource,
): WalletAccount {
    override val icon: Int = R.drawable.ic_item_zashi
}

data class KeystoneAccount(
    override val sdkAccount: Account,
    override val unifiedAddress: WalletAddress.Unified,
    override val saplingAddress: WalletAddress.Sapling,
    override val transparentAddress: WalletAddress.Transparent,
    override val saplingBalance: WalletBalance,
    override val orchardBalance: WalletBalance,
    override val transparentBalance: Zatoshi,
    override val isSelected: Boolean,
    override val name: StringResource,
): WalletAccount {
    override val icon: Int = R.drawable.ic_item_keystone
}
