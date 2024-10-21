package co.electriccoin.zcash.ui.screen.paymentrequest.model

sealed class PaymentRequestStage {
    data object Initial : PaymentRequestStage()
    data object Sending : PaymentRequestStage()
    data object Confirmed : PaymentRequestStage()
    data class Failure(
        val error: String,
        val stackTrace: String,
    ) : PaymentRequestStage()
    data object FailureGrpc : PaymentRequestStage()
}