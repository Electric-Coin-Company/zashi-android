package co.electriccoin.zcash.ui.screen.balances.model

import androidx.compose.runtime.saveable.mapSaver

sealed class ShieldState {
    data object None : ShieldState()

    data object Available : ShieldState()

    data object Running : ShieldState()

    data object Shielded : ShieldState()

    data class Failed(val error: String) : ShieldState()

    fun isEnabled() = this != Running && this !is Failed && this != Shielded

    companion object {
        private const val TYPE_NONE = "none" // $NON-NLS
        private const val TYPE_AVAILABLE = "available" // $NON-NLS
        private const val TYPE_RUNNING = "running" // $NON-NLS
        private const val TYPE_SHIELDED = "shielded" // $NON-NLS
        private const val TYPE_FAILED = "failed" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS

        private const val KEY_ERROR = "error" // $NON-NLS

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
                                    TYPE_NONE -> None
                                    TYPE_AVAILABLE -> Available
                                    TYPE_RUNNING -> Running
                                    TYPE_SHIELDED -> Shielded
                                    TYPE_FAILED -> Failed((it[KEY_ERROR] as String))
                                    else -> null
                                }
                            }
                        }
                    )
                }

        private fun ShieldState.toSaverMap(): HashMap<String, String> {
            val saverMap = HashMap<String, String>()
            when (this) {
                None -> saverMap[KEY_TYPE] = TYPE_NONE
                Available -> saverMap[KEY_TYPE] = TYPE_AVAILABLE
                Running -> saverMap[KEY_TYPE] = TYPE_RUNNING
                Shielded -> saverMap[KEY_TYPE] = TYPE_SHIELDED
                is Failed -> {
                    saverMap[KEY_TYPE] = TYPE_FAILED
                    saverMap[KEY_ERROR] = this.error
                }
            }

            return saverMap
        }
    }
}
