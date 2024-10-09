package co.electriccoin.zcash.ui.screen.request.model

import androidx.compose.ui.graphics.ImageBitmap
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi

internal sealed class RequestState {
    data object Loading : RequestState()

    sealed class Prepared(
        open val onBack: () -> Unit,
        open val onDone: () -> Unit,
    ) : RequestState()

    data class Amount(
        val request: Request,
        val onAmount: (Zatoshi) -> Unit,
        override val onBack: () -> Unit,
        override val onDone: () -> Unit,
    ) : Prepared(onBack, onDone)

    data class Memo(
        val request: Request,
        val walletAddress: WalletAddress,
        val onMemo: (String) -> Unit,
        override val onBack: () -> Unit,
        override val onDone: () -> Unit,
    ) : Prepared(onBack, onDone)

    data class QrCode(
        val request: Request,
        val walletAddress: WalletAddress,
        val onQrCodeShare: (ImageBitmap) -> Unit,
        override val onBack: () -> Unit,
        override val onDone: () -> Unit,
    ) : Prepared(onBack, onDone)
}
