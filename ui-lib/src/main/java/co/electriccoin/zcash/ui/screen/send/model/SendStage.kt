package co.electriccoin.zcash.ui.screen.send.model

import androidx.compose.runtime.saveable.mapSaver

sealed class SendStage {
    data object Form : SendStage()

    data object Confirmation : SendStage()

    data object Sending : SendStage()

    data class SendFailure(val error: String) : SendStage()

    data object SendSuccessful : SendStage()

    companion object {
        private const val TYPE_FORM = "form" // $NON-NLS
        private const val TYPE_CONFIRMATION = "confirmation" // $NON-NLS
        private const val TYPE_SENDING = "sending" // $NON-NLS
        private const val TYPE_FAILURE = "failure" // $NON-NLS
        private const val TYPE_SUCCESSFUL = "successful" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS
        private const val KEY_ERROR = "error" // $NON-NLS

        internal val Saver
            get() =
                run {
                    mapSaver<SendStage>(
                        save = { it.toSaverMap() },
                        restore = {
                            if (it.isEmpty()) {
                                null
                            } else {
                                val sendStageString = (it[KEY_TYPE] as String)
                                when (sendStageString) {
                                    TYPE_FORM -> Form
                                    TYPE_CONFIRMATION -> Confirmation
                                    TYPE_SENDING -> Sending
                                    TYPE_FAILURE -> SendFailure((it[KEY_ERROR] as String))
                                    TYPE_SUCCESSFUL -> SendSuccessful
                                    else -> null
                                }
                            }
                        }
                    )
                }

        private fun SendStage.toSaverMap(): HashMap<String, String> {
            val saverMap = HashMap<String, String>()
            when (this) {
                Form -> saverMap[KEY_TYPE] = TYPE_FORM
                Confirmation -> saverMap[KEY_TYPE] = TYPE_CONFIRMATION
                is SendFailure -> {
                    saverMap[KEY_TYPE] = TYPE_FAILURE
                    saverMap[KEY_ERROR] = this.error
                }
                SendSuccessful -> saverMap[KEY_TYPE] = TYPE_SUCCESSFUL
                Sending -> saverMap[KEY_TYPE] = TYPE_SENDING
            }

            return saverMap
        }
    }
}
