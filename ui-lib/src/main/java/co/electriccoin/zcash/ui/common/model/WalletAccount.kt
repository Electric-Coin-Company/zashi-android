package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint

sealed interface WalletAccount {

    val sdkAccount: Account

    val unifiedAddress: WalletAddress.Unified
    val saplingAddress: WalletAddress.Sapling
    val transparentAddress: WalletAddress.Transparent
    val saplingBalance: WalletBalance
    val orchardBalance: WalletBalance
    val transparentBalance: Zatoshi
    val isSelected: Boolean

    val network: ZcashNetwork?
    val endpoint: LightWalletEndpoint?
    val birthday: BlockHeight?
    val seedPhrase: SeedPhrase?
    val walletInitMode: WalletInitMode?
    val spendingKey: UnifiedSpendingKey?

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
    val persistableWallet: PersistableWallet,
    override val spendingKey: UnifiedSpendingKey?,
): WalletAccount {
    override val network: ZcashNetwork = persistableWallet.network
    override val endpoint: LightWalletEndpoint = persistableWallet.endpoint
    override val birthday: BlockHeight? = persistableWallet.birthday
    override val seedPhrase: SeedPhrase = persistableWallet.seedPhrase
    override val walletInitMode: WalletInitMode = persistableWallet.walletInitMode
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
    override val network: ZcashNetwork,
    override val endpoint: LightWalletEndpoint,
): WalletAccount {
    // keystone has no birthday, seed phrase, or wallet init mode
    override val birthday: BlockHeight? = null
    override val seedPhrase: SeedPhrase? = null
    override val walletInitMode: WalletInitMode? = null
    override val spendingKey: UnifiedSpendingKey? = null
}