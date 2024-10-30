package co.electriccoin.zcash.ui.screen.sendconfirmation.model

import androidx.compose.runtime.saveable.mapSaver

sealed class SendConfirmationStage {
    data object Prepared : SendConfirmationStage()

    data object Sending : SendConfirmationStage()

    data object Success : SendConfirmationStage()

    data class Failure(
        val error: String,
        val stackTrace: String,
    ) : SendConfirmationStage()

    data object FailureGrpc : SendConfirmationStage()

    data object MultipleTrxFailure : SendConfirmationStage()

    data object MultipleTrxFailureReported : SendConfirmationStage()

    fun toStringName(): String {
        return when (this) {
            Prepared -> TYPE_PREPARED
            is Failure -> TYPE_FAILURE
            is FailureGrpc -> TYPE_FAILURE_GRPC
            MultipleTrxFailure -> TYPE_MULTIPLE_TRX_FAILURE
            MultipleTrxFailureReported -> TYPE_MULTIPLE_TRX_FAILURE_REPORTED
            Sending -> TYPE_SENDING
            Success -> TYPE_SUCCESS
        }
    }

    companion object {
        private const val TYPE_PREPARED = "prepared" // $NON-NLS
        private const val TYPE_SENDING = "sending" // $NON-NLS
        private const val TYPE_SUCCESS = "success" // $NON-NLS
        private const val TYPE_FAILURE = "failure" // $NON-NLS
        private const val TYPE_FAILURE_GRPC = "type_failure_grpc" // $NON-NLS
        private const val TYPE_MULTIPLE_TRX_FAILURE = "multiple_trx_failure" // $NON-NLS
        private const val TYPE_MULTIPLE_TRX_FAILURE_REPORTED = "multiple_trx_failure_reported" // $NON-NLS

        private const val KEY_TYPE = "type" // $NON-NLS
        private const val KEY_ERROR = "error" // $NON-NLS
        private const val KEY_STACKTRACE = "stacktrace" // $NON-NLS

        internal val Saver
            get() =
                run {
                    mapSaver<SendConfirmationStage>(
                        save = { it.toSaverMap() },
                        restore = {
                            if (it.isEmpty()) {
                                null
                            } else {
                                val sendStageString = (it[KEY_TYPE] as String)
                                when (sendStageString) {
                                    TYPE_PREPARED -> Prepared
                                    TYPE_SENDING -> Sending
                                    TYPE_SUCCESS -> Success
                                    TYPE_FAILURE ->
                                        Failure(
                                            error = (it[KEY_ERROR] as String),
                                            stackTrace = (it[KEY_STACKTRACE] as String)
                                        )
                                    TYPE_FAILURE_GRPC -> FailureGrpc
                                    TYPE_MULTIPLE_TRX_FAILURE -> MultipleTrxFailure
                                    TYPE_MULTIPLE_TRX_FAILURE_REPORTED -> MultipleTrxFailureReported
                                    else -> null
                                }
                            }
                        }
                    )
                }

        private fun SendConfirmationStage.toSaverMap(): HashMap<String, String> {
            val saverMap = HashMap<String, String>()
            when (this) {
                Prepared -> saverMap[KEY_TYPE] = TYPE_PREPARED
                Sending -> saverMap[KEY_TYPE] = TYPE_SENDING
                Success -> saverMap[KEY_TYPE] = TYPE_SUCCESS
                is Failure -> {
                    saverMap[KEY_TYPE] = TYPE_FAILURE
                    saverMap[KEY_ERROR] = this.error
                    saverMap[KEY_STACKTRACE] = this.stackTrace
                }
                is FailureGrpc -> saverMap[KEY_TYPE] = TYPE_FAILURE
                is MultipleTrxFailure -> saverMap[KEY_TYPE] = TYPE_MULTIPLE_TRX_FAILURE
                is MultipleTrxFailureReported -> saverMap[KEY_TYPE] = TYPE_MULTIPLE_TRX_FAILURE_REPORTED
            }

            return saverMap
        }

        fun fromStringName(stringName: String?): SendConfirmationStage {
            return when (stringName) {
                TYPE_PREPARED -> Prepared
                TYPE_SENDING -> Sending
                TYPE_SUCCESS -> Success
                // Add the String error and stackTrace parameter storing and retrieving
                TYPE_FAILURE -> Failure("", "")
                TYPE_FAILURE_GRPC -> FailureGrpc
                TYPE_MULTIPLE_TRX_FAILURE -> MultipleTrxFailure
                TYPE_MULTIPLE_TRX_FAILURE_REPORTED -> MultipleTrxFailureReported
                else -> Prepared
            }
        }
    }
}
