package co.electriccoin.zcash.ui.screen.send.model

data class SendArgumentsWrapper(
    val recipientAddress: String? = null,
    val amount: String? = null,
    val memo: String? = null
)
