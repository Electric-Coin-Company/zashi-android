package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.model.WalletAddress

enum class AddressType {
    UNIFIED, TRANSPARENT, SAPLING, TEX;

    suspend fun toWalletAddress(address: String) = when (this) {
        UNIFIED -> WalletAddress.Unified.new(address)
        TRANSPARENT -> WalletAddress.Transparent.new(address)
        SAPLING -> WalletAddress.Sapling.new(address)
        TEX -> WalletAddress.Tex.new(address)
    }

    companion object {
        fun fromWalletAddress(walletAddress: WalletAddress) = when (walletAddress) {
            is WalletAddress.Sapling -> SAPLING
            is WalletAddress.Tex -> TEX
            is WalletAddress.Transparent -> TRANSPARENT
            is WalletAddress.Unified -> UNIFIED
        }
    }
}