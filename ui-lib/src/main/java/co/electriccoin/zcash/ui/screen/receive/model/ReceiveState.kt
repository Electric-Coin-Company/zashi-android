package co.electriccoin.zcash.ui.screen.receive.model

import cash.z.ecc.android.sdk.model.WalletAddresses

internal sealed class ReceiveState {
    data object Loading : ReceiveState()

    data class Prepared(
        val walletAddresses: WalletAddresses,
        val onAddressCopy: (String) -> Unit,
        val onQrCode: (ReceiveAddressType) -> Unit,
        val onRequest: (ReceiveAddressType) -> Unit,
        val isTestnet: Boolean,
    ) : ReceiveState()
}
