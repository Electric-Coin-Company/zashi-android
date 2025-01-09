package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import kotlinx.coroutines.runBlocking
import org.zecdev.zip321.ZIP321

internal class Zip321ParseUriValidationUseCase(
    private val getSynchronizerUseCase: GetSynchronizerUseCase
) {
    operator fun invoke(zip321Uri: String) = validateZip321Uri(zip321Uri)

    private fun validateZip321Uri(zip321Uri: String): Zip321ParseUriValidation {
        val paymentRequest =
            runCatching {
                ZIP321.request(
                    uriString = zip321Uri,
                    validatingRecipients = { address ->
                        // We should be fine with the blocking implementation here
                        runBlocking {
                            getSynchronizerUseCase().validateAddress(address).let { validation ->
                                when (validation) {
                                    is AddressType.Invalid -> {
                                        Twig.error { "Address from Zip321 validation failed: ${validation.reason}" }
                                        false
                                    }
                                    else -> {
                                        validation is AddressType.Valid
                                    }
                                }
                            }
                        }
                    }
                )
            }.onFailure {
                Twig.debug { "Not valid Zip321 URI scanned" }
            }.getOrElse {
                false
            }

        Twig.info { "Payment Request Zip321 validation result: $paymentRequest." }

        return when (paymentRequest) {
            is ZIP321.ParserResult.Request -> Zip321ParseUriValidation.Valid(zip321Uri)
            is ZIP321.ParserResult.SingleAddress ->
                Zip321ParseUriValidation.SingleAddress(paymentRequest.singleRecipient.value)
            else -> Zip321ParseUriValidation.Invalid
        }
    }

    internal sealed class Zip321ParseUriValidation {
        data class Valid(val zip321Uri: String) : Zip321ParseUriValidation()

        data class SingleAddress(val address: String) : Zip321ParseUriValidation()

        data object Invalid : Zip321ParseUriValidation()
    }
}
