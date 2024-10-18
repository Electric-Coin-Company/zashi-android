package co.electriccoin.zcash.ui.screen.paymentrequest.model

import cash.z.ecc.android.sdk.model.MonetarySeparators
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationArguments

internal sealed class PaymentRequestState {
    data object Loading : PaymentRequestState()

    data class Prepared(
        val zip321Uri: String,
        val arguments: SendConfirmationArguments,
        val monetarySeparators: MonetarySeparators,
        val onClose: () -> Unit,
        val onSend: (zip321Uri: String) -> Unit,
    ) : PaymentRequestState()
}
