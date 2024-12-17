package co.electriccoin.zcash.ui.screen.qrcode.model

import androidx.compose.ui.graphics.ImageBitmap
import cash.z.ecc.android.sdk.model.WalletAddress

internal sealed class QrCodeState {
    data object Loading : QrCodeState()

    data class Prepared(
        val qrCodeType: QrCodeType,
        val walletAddress: WalletAddress,
        val onAddressCopy: (String) -> Unit,
        val onQrCodeShare: (ImageBitmap) -> Unit,
        val onBack: () -> Unit,
    ) : QrCodeState()
}

enum class QrCodeType {
    ZASHI,
    KEYSTONE
}
