package co.electriccoin.zcash.ui.screen.request.model

import androidx.compose.ui.graphics.ImageBitmap
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

internal sealed class RequestState {
    data object Loading : RequestState()

    sealed class Prepared(open val onBack: () -> Unit) : RequestState()

    data class Amount(
        val request: Request,
        val exchangeRateState: ExchangeRateState,
        val zcashCurrency: ZcashCurrency,
        val monetarySeparators: MonetarySeparators,
        val onAmount: (OnAmount) -> Unit,
        val onSwitch: (RequestCurrency) -> Unit,
        override val onBack: () -> Unit,
        val onDone: () -> Unit,
    ) : Prepared(onBack)

    data class Memo(
        val request: Request,
        val walletAddress: WalletAddress,
        val zcashCurrency: ZcashCurrency,
        val onMemo: (MemoState) -> Unit,
        val onDone: () -> Unit,
        override val onBack: () -> Unit,
    ) : Prepared(onBack)

    data class QrCode(
        val request: Request,
        val walletAddress: WalletAddress,
        val onQrCodeShare: (ImageBitmap) -> Unit,
        override val onBack: () -> Unit,
        val onClose: () -> Unit,
        val zcashCurrency: ZcashCurrency,
    ) : Prepared(onBack)
}
