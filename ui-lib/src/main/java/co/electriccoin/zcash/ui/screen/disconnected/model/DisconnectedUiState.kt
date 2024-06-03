package co.electriccoin.zcash.ui.screen.disconnected.model

import androidx.compose.runtime.saveable.mapSaver

internal sealed class DisconnectedUiState {
    data object Displayed : DisconnectedUiState()

    data object Dismissed : DisconnectedUiState()

    companion object {
        private const val TYPE_DISPLAYED = "displayed" // $NON-NLS
        private const val TYPE_DISMISSED = "dismissed" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS

        internal val Saver
            get() =
                run {
                    mapSaver(
                        save = { it.toSaverMap() },
                        restore = {
                            if (it.isEmpty()) {
                                null
                            } else {
                                val sendStageString = (it[KEY_TYPE] as String)
                                when (sendStageString) {
                                    TYPE_DISPLAYED -> Displayed
                                    TYPE_DISMISSED -> Dismissed
                                    else -> null
                                }
                            }
                        }
                    )
                }

        private fun DisconnectedUiState.toSaverMap(): HashMap<String, String> {
            val saverMap = HashMap<String, String>()
            when (this) {
                Displayed -> saverMap[KEY_TYPE] = TYPE_DISPLAYED
                Dismissed -> saverMap[KEY_TYPE] = TYPE_DISMISSED
            }
            return saverMap
        }
    }
}
