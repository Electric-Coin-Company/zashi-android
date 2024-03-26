package co.electriccoin.zcash.ui.screen.sendconfirmation.model

import androidx.compose.runtime.saveable.mapSaver

sealed class SendConfirmationStage {
    data object Confirmation : SendConfirmationStage()

    data object Sending : SendConfirmationStage()

    data class Failure(val error: String?) : SendConfirmationStage()

    data object MultipleTrxFailure : SendConfirmationStage()

    data object MultipleTrxFailureReported : SendConfirmationStage()

    fun toStringName(): String {
        return when (this) {
            Confirmation -> TYPE_CONFIRMATION
            is Failure -> TYPE_FAILURE
            MultipleTrxFailure -> TYPE_MULTIPLE_TRX_FAILURE
            MultipleTrxFailureReported -> TYPE_MULTIPLE_TRX_FAILURE_REPORTED
            Sending -> TYPE_SENDING
        }
    }

    companion object {
        private const val TYPE_CONFIRMATION = "confirmation" // $NON-NLS
        private const val TYPE_SENDING = "sending" // $NON-NLS
        private const val TYPE_FAILURE = "failure" // $NON-NLS
        private const val TYPE_MULTIPLE_TRX_FAILURE = "multiple_trx_failure" // $NON-NLS
        private const val TYPE_MULTIPLE_TRX_FAILURE_REPORTED = "multiple_trx_failure_reported" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS
        private const val KEY_ERROR = "error" // $NON-NLS

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
                                    TYPE_CONFIRMATION -> Confirmation
                                    TYPE_SENDING -> Sending
                                    TYPE_FAILURE -> Failure((it[KEY_ERROR] as String))
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
                Confirmation -> saverMap[KEY_TYPE] = TYPE_CONFIRMATION
                Sending -> saverMap[KEY_TYPE] = TYPE_SENDING
                is Failure -> {
                    saverMap[KEY_TYPE] = TYPE_FAILURE
                    saverMap[KEY_ERROR] = this.error ?: ""
                }
                is MultipleTrxFailure -> saverMap[KEY_TYPE] = TYPE_MULTIPLE_TRX_FAILURE
                is MultipleTrxFailureReported -> saverMap[KEY_TYPE] = TYPE_MULTIPLE_TRX_FAILURE_REPORTED
            }

            return saverMap
        }

        fun fromStringName(stringName: String?): SendConfirmationStage {
            return when (stringName) {
                TYPE_CONFIRMATION -> Confirmation
                TYPE_SENDING -> Sending
                // Add the String error parameter storing and retrieving
                TYPE_FAILURE -> Failure(null)
                TYPE_MULTIPLE_TRX_FAILURE -> MultipleTrxFailure
                TYPE_MULTIPLE_TRX_FAILURE_REPORTED -> MultipleTrxFailureReported
                else -> Confirmation
            }
        }
    }
}
