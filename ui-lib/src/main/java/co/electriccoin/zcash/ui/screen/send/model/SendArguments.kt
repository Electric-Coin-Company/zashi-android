package co.electriccoin.zcash.ui.screen.send.model

data class SendArguments(
    val recipientAddress: RecipientAddressState? = null,
    val zip321Uri: String? = null,
    val clearForm: Boolean = false,
)
