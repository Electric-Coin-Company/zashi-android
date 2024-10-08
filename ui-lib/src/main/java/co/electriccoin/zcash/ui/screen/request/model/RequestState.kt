package co.electriccoin.zcash.ui.screen.request.model

import androidx.compose.ui.graphics.ImageBitmap
import cash.z.ecc.android.sdk.model.WalletAddress

internal sealed class RequestState {
    data object Loading : RequestState()

    data class Prepared(
        val walletAddress: WalletAddress,
        val onAmount: (Request) -> Unit,
        val onRequest: (Request) -> Unit,
        val onQrCodeShare: (ImageBitmap) -> Unit,
        val onBack: () -> Unit,
    ) : RequestState()
}
