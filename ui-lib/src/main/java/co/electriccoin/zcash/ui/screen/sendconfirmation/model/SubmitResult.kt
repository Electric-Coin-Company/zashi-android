package co.electriccoin.zcash.ui.screen.sendconfirmation.model

sealed class SubmitResult {
    data object Success : SubmitResult()

    data class SimpleTrxFailure(val errorDescription: String) : SubmitResult()

    data object MultipleTrxFailure : SubmitResult()
}
