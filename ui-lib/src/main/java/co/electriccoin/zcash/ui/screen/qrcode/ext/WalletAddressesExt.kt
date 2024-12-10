package co.electriccoin.zcash.ui.screen.qrcode.ext

import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType

internal fun WalletAccount.fromReceiveAddressType(receiveAddressType: ReceiveAddressType) =
    when (receiveAddressType) {
        ReceiveAddressType.Unified -> this.unified.address
        ReceiveAddressType.Sapling -> this.sapling?.address
        ReceiveAddressType.Transparent -> this.transparent.address
    }
