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

sealed interface WalletAccount : Comparable<WalletAccount> {
    val sdkAccount: Account

    val unified: UnifiedInfo
    val sapling: SaplingInfo?
    val transparent: TransparentInfo
    val isSelected: Boolean
    val name: StringResource

    @get:DrawableRes
    val icon: Int

    val hdAccountIndex: Zip32AccountIndex
        get() = sdkAccount.hdAccountIndex!!

    val totalBalance: Zatoshi
    val totalShieldedBalance: Zatoshi
    val spendableBalance: Zatoshi
    val changePendingBalance: Zatoshi
    val valuePendingBalance: Zatoshi

    val pendingBalance: Zatoshi
        get() = changePendingBalance + valuePendingBalance

    val hasChangePending: Boolean
    val hasValuePending: Boolean
    val isPending: Boolean
        get() = pendingBalance > Zatoshi(0)

    fun canSpend(amount: Zatoshi): Boolean = spendableBalance >= amount

    fun isProcessingZeroAvailableBalance(): Boolean {
        if (totalShieldedBalance == Zatoshi(0) && transparent.balance > Zatoshi(0)) {
            return false
        }

        return totalBalance != totalShieldedBalance && totalShieldedBalance == Zatoshi(0)
    }
}

data class ZashiAccount(
    override val sdkAccount: Account,
    override val unified: UnifiedInfo,
    override val sapling: SaplingInfo,
    override val transparent: TransparentInfo,
    override val isSelected: Boolean,
) : WalletAccount {
    override val name: StringResource
        get() = stringRes(co.electriccoin.zcash.ui.R.string.zashi_wallet_name)
    override val icon: Int
        get() = R.drawable.ic_item_zashi
    override val totalBalance: Zatoshi
        get() = unified.balance.total + sapling.balance.total + transparent.balance
    override val totalShieldedBalance: Zatoshi
        get() = unified.balance.total + sapling.balance.total
    override val spendableBalance: Zatoshi
        get() = unified.balance.available + sapling.balance.available
    override val changePendingBalance: Zatoshi
        get() = unified.balance.changePending + sapling.balance.changePending
    override val valuePendingBalance: Zatoshi
        get() = unified.balance.valuePending + sapling.balance.valuePending
    override val hasChangePending: Boolean
        get() = changePendingBalance.value > 0L
    override val hasValuePending: Boolean
        get() = valuePendingBalance.value > 0L

    override fun compareTo(other: WalletAccount) =
        when (other) {
            is KeystoneAccount -> 1
            is ZashiAccount -> 0
        }
}

data class KeystoneAccount(
    override val sdkAccount: Account,
    override val unified: UnifiedInfo,
    override val transparent: TransparentInfo,
    override val isSelected: Boolean,
) : WalletAccount {
    override val icon: Int
        get() = R.drawable.ic_item_keystone
    override val name: StringResource
        get() = stringRes(co.electriccoin.zcash.ui.R.string.keystone_wallet_name)
    override val sapling: SaplingInfo? = null
    override val totalBalance: Zatoshi
        get() = unified.balance.total + transparent.balance
    override val totalShieldedBalance: Zatoshi
        get() = unified.balance.total
    override val spendableBalance: Zatoshi
        get() = unified.balance.available
    override val changePendingBalance: Zatoshi
        get() = unified.balance.changePending
    override val valuePendingBalance: Zatoshi
        get() = unified.balance.valuePending
    override val hasChangePending: Boolean
        get() = changePendingBalance.value > 0L
    override val hasValuePending: Boolean
        get() = valuePendingBalance.value > 0L

    override fun compareTo(other: WalletAccount) =
        when (other) {
            is KeystoneAccount -> 0
            is ZashiAccount -> -1
        }
}

data class UnifiedInfo(
    val address: WalletAddress.Unified,
    val balance: WalletBalance
)

data class TransparentInfo(
    val address: WalletAddress.Transparent,
    val balance: Zatoshi
) {
    val isShieldingAvailable: Boolean
        get() = balance > Zatoshi(100000L)
}

data class SaplingInfo(
    val address: WalletAddress.Sapling,
    val balance: WalletBalance
)
