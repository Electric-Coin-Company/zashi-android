package co.electriccoin.zcash.ui.screen.qrcode.model

import cash.z.ecc.android.sdk.model.WalletAddress

internal sealed class QrCodeState {
    data object Loading : QrCodeState()
    data class Prepared(
        val walletAddress: WalletAddress,
        val onAddressCopy: (String) -> Unit,
        val onQrCodeShare: (String) -> Unit,
        val onBack: () -> Unit,
    ) : QrCodeState()
}
