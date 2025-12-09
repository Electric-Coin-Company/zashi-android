package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.NetworkDimension
import co.electriccoin.zcash.ui.common.model.VersionInfo
import org.zecdev.zip321.ZIP321
import org.zecdev.zip321.model.MemoBytes
import org.zecdev.zip321.model.NonNegativeAmount
import org.zecdev.zip321.model.Payment
import org.zecdev.zip321.model.PaymentRequest
import org.zecdev.zip321.model.RecipientAddress
import org.zecdev.zip321.parser.ParserContext
import java.math.BigDecimal

class Zip321BuildUriUseCase {
    operator fun invoke(
        address: String,
        amount: BigDecimal,
        memo: String,
    ) = buildUri(
        address = address,
        amount = amount,
        memo = memo,
    )

    private fun buildUri(
        address: String,
        amount: BigDecimal,
        memo: String,
    ): String {
        val payment =
            Payment(
                recipientAddress =
                    RecipientAddress(
                        value = address,
                        network =
                            when (VersionInfo.NETWORK_DIMENSION) {
                                NetworkDimension.MAINNET -> ParserContext.MAINNET
                                NetworkDimension.TESTNET -> ParserContext.TESTNET
                            }
                    ),
                nonNegativeAmount = NonNegativeAmount(amount),
                memo =
                    if (memo.isBlank()) {
                        null
                    } else {
                        MemoBytes(memo)
                    },
                otherParams = null,
                label = null,
                message = null
            )

        val paymentRequest = PaymentRequest(payments = listOf(payment))

        val zip321Uri =
            ZIP321.uriString(
                paymentRequest,
                ZIP321.FormattingOptions.UseEmptyParamIndex(omitAddressLabel = true)
            )

        Twig.debug { "Request Zip321 uri: $zip321Uri" }

        return zip321Uri
    }
}
