package co.electriccoin.zcash.ui.screen.send.nighthawk.model

data class SendAndReviewUiState(
    val amountToSend: String = "0.0",
    val amountUnit: String = "ZEC",
    val convertedAmountWithCurrency: String = "55 EUR",
    val memo: String = "",
    val network: String = "MainNet",
    val recipientType: String = "Shielded",
    val receiverAddress: String = "",
    val subTotal: String = "",
    val networkFees: String = "",
    val totalAmount: String = ""
)