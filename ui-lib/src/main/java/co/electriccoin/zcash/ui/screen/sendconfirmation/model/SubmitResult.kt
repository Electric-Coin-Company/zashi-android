package co.electriccoin.zcash.ui.screen.sendconfirmation.model

import cash.z.ecc.android.sdk.model.TransactionSubmitResult

sealed class SubmitResult {
    data object Success : SubmitResult()

    data object MultipleTrxFailure : SubmitResult()

    sealed class SimpleTrxFailure : SubmitResult() {
        abstract fun toErrorMessage(): String

        abstract fun toErrorStacktrace(): String

        data class SimpleTrxFailureGrpc(val result: TransactionSubmitResult.Failure) : SimpleTrxFailure() {
            // Currently, we intentionally do not include any error related details
            override fun toErrorMessage() = ""

            override fun toErrorStacktrace() = ""
        }

        data class SimpleTrxFailureSubmit(val result: TransactionSubmitResult.Failure) : SimpleTrxFailure() {
            override fun toErrorMessage() =
                buildString {
                    appendLine("Error code: ${result.code}")
                    appendLine(result.description ?: "Unknown error")
                }

            override fun toErrorStacktrace(): String = toErrorMessage()
        }

        data class SimpleTrxFailureOther(val error: Throwable) : SimpleTrxFailure() {
            override fun toErrorMessage() = error.message ?: "Unknown error"

            override fun toErrorStacktrace(): String = error.stackTraceToString()
        }
    }
}
