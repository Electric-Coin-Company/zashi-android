package co.electriccoin.zcash.ui.screen.receive.ext

import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType

internal fun WalletAddress.toReceiveAddressType() = when (this) {
    is WalletAddress.Unified -> ReceiveAddressType.Unified
    is WalletAddress.Sapling -> ReceiveAddressType.Sapling
    is WalletAddress.Transparent -> ReceiveAddressType.Transparent
    else -> error("Unsupported address type")
}