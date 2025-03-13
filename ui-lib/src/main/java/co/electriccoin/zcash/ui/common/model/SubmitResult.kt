package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.TransactionSubmitResult

sealed interface SubmitResult {
    data class Success(
        val txIds: List<String>
    ) : SubmitResult

    data class MultipleTrxFailure(
        val results: List<TransactionSubmitResult>
    ) : SubmitResult

    sealed interface SimpleTrxFailure : SubmitResult {
        fun toErrorMessage(): String

        fun toErrorStacktrace(): String

        data class SimpleTrxFailureGrpc(
            val result: TransactionSubmitResult.Failure
        ) : SimpleTrxFailure {
            // Currently, we intentionally do not include any error related details
            override fun toErrorMessage() = ""

            override fun toErrorStacktrace() = ""
        }

        data class SimpleTrxFailureSubmit(
            val result: TransactionSubmitResult.Failure
        ) : SimpleTrxFailure {
            override fun toErrorMessage() =
                buildString {
                    appendLine("Error code: ${result.code}")
                    appendLine(result.description ?: "Unknown error")
                }

            override fun toErrorStacktrace(): String = toErrorMessage()
        }

        data class SimpleTrxFailureOther(
            val error: Throwable
        ) : SimpleTrxFailure {
            override fun toErrorMessage() = error.message ?: "Unknown error"

            override fun toErrorStacktrace(): String = error.stackTraceToString()
        }
    }
}
