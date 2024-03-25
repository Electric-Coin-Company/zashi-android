package co.electriccoin.zcash.ui.screen.send.model

data class SendArguments(
    val recipientAddress: RecipientAddressState? = null,
    val clearForm: Boolean = false,
)
