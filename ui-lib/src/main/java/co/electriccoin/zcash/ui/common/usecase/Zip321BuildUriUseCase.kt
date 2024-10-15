package co.electriccoin.zcash.ui.common.usecase

import MemoBytes
import NonNegativeAmount
import Payment
import PaymentRequest
import RecipientAddress
import co.electriccoin.zcash.spackle.Twig
import org.zecdev.zip321.ZIP321

class Zip321BuildUriUseCase {
    operator fun invoke(
        address: String,
        amount: String,
        memo: String,
    ) = buildUri(
        address = address,
        amount = amount,
        memo = memo,
    )

    private fun buildUri(
        address: String,
        amount: String,
        memo: String,
    ): String {
        val payment = Payment(
            recipientAddress = RecipientAddress(address),
            nonNegativeAmount = NonNegativeAmount(amount),
            memo = if (memo.isBlank()) { null } else  { MemoBytes(memo) },
            otherParams = null,
            label = null,
            message = null
        )

        val paymentRequest = PaymentRequest(payments = listOf(payment))

        val zip321Uri = ZIP321.uriString(
            paymentRequest,
            ZIP321.FormattingOptions.UseEmptyParamIndex(omitAddressLabel = true)
        )

        Twig.info { "Request Zip321 uri: $zip321Uri" }

        return zip321Uri
    }
}
