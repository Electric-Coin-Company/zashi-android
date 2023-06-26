package co.electriccoin.zcash.ui.screen.send.nighthawk.model

data class EnterZecUIState(
    val enteredAmount: String = "0",
    val amountUnit: String = "ZEC",
    val spendableBalance: String = "--",
    val convertedAmount: String? = null,
    val isEnoughBalance: Boolean = true,
    val isScanPaymentCodeOptionAvailable: Boolean = enteredAmount == "0" && isEnoughBalance
)

