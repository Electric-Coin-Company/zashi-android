package co.electriccoin.zcash.ui.screen.paymentrequest.model

import androidx.compose.ui.graphics.ImageBitmap
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

internal sealed class PaymentRequestState {
    data object Loading : PaymentRequestState()

    sealed class Prepared(open val onBack: () -> Unit) : PaymentRequestState()

    data class Amount(

        val exchangeRateState: ExchangeRateState,
        val zcashCurrency: ZcashCurrency,
        val monetarySeparators: MonetarySeparators,

        override val onBack: () -> Unit,
        val onDone: () -> Unit,
    ) : Prepared(onBack)

    data class Memo(

        val walletAddress: WalletAddress,
        val zcashCurrency: ZcashCurrency,

        val onDone: () -> Unit,
        override val onBack: () -> Unit,
    ) : Prepared(onBack)

    data class QrCode(

        val walletAddress: WalletAddress,
        val onQrCodeShare: (ImageBitmap) -> Unit,
        val onQrCodeGenerate: (pixels: Int) -> Unit,
        override val onBack: () -> Unit,
        val onClose: () -> Unit,
        val zcashCurrency: ZcashCurrency,
    ) : Prepared(onBack)
}
