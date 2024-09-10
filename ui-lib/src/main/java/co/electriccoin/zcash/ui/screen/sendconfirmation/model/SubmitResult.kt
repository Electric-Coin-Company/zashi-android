package co.electriccoin.zcash.ui.screen.sendconfirmation.model

import cash.z.ecc.android.sdk.model.TransactionSubmitResult

sealed class SubmitResult {
    data object Success : SubmitResult()

    data object MultipleTrxFailure : SubmitResult()

    sealed class SimpleTrxFailure : SubmitResult() {
        abstract fun toErrorDescription(): String

        data class SimpleTrxFailureGrpc(val result: TransactionSubmitResult.Failure) : SimpleTrxFailure() {
            // Currently, we intentionally do not include any error related details
            override fun toErrorDescription() = ""
        }

        data class SimpleTrxFailureSubmit(val result: TransactionSubmitResult.Failure) : SimpleTrxFailure() {
            override fun toErrorDescription() =
                buildString {
                    appendLine("Error code: ${result.code}")
                    appendLine(result.description ?: "Unknown error")
                }
        }

        data class SimpleTrxFailureOther(val error: Throwable) : SimpleTrxFailure() {
            override fun toErrorDescription() = error.message ?: "Unknown error"
        }
    }
}
