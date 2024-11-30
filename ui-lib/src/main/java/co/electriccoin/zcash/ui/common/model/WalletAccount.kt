package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi

sealed interface WalletAccount {

    val sdkAccount: Account

    val unifiedAddress: WalletAddress.Unified
    val saplingAddress: WalletAddress.Sapling
    val transparentAddress: WalletAddress.Transparent

    val saplingBalance: WalletBalance
    val orchardBalance: WalletBalance
    val transparentBalance: Zatoshi

    val isSelected: Boolean

    data class Zashi(
        override val sdkAccount: Account,
        override val unifiedAddress: WalletAddress.Unified,
        override val saplingAddress: WalletAddress.Sapling,
        override val transparentAddress: WalletAddress.Transparent,
        override val saplingBalance: WalletBalance,
        override val orchardBalance: WalletBalance,
        override val transparentBalance: Zatoshi,
        override val isSelected: Boolean,
    ): WalletAccount

    data class Keystone(
        override val sdkAccount: Account,
        override val unifiedAddress: WalletAddress.Unified,
        override val saplingAddress: WalletAddress.Sapling,
        override val transparentAddress: WalletAddress.Transparent,
        override val saplingBalance: WalletBalance,
        override val orchardBalance: WalletBalance,
        override val transparentBalance: Zatoshi,
        override val isSelected: Boolean,
    ): WalletAccount
}
