package co.electriccoin.zcash.ui.screen.paymentrequest.model

import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

internal sealed class PaymentRequestState {
    data object Loading : PaymentRequestState()

    data class Prepared(
        val arguments: PaymentRequestArguments,
        val zecSend: ZecSend,
        val monetarySeparators: MonetarySeparators,
        val onClose: () -> Unit,
        val onSend: (zip321Uri: String) -> Unit,
        val exchangeRateState: ExchangeRateState,
    ) : PaymentRequestState()
}
