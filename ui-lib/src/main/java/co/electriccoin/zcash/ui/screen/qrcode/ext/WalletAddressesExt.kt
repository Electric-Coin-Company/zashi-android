package co.electriccoin.zcash.ui.screen.qrcode.ext

import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType

internal fun WalletAddresses.fromReceiveAddressType(receiveAddressType: ReceiveAddressType) =
    when (receiveAddressType) {
        ReceiveAddressType.Unified -> unified
        ReceiveAddressType.Sapling -> sapling
        ReceiveAddressType.Transparent -> transparent
    }
