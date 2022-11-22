package co.electriccoin.zcash.ui.screen.backup.ext

import androidx.compose.runtime.saveable.mapSaver
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState

private const val KEY_STAGE = "stage" // $NON-NLS

internal val BackupState.Companion.Saver
    get() = mapSaver(
        save = { it.toSaverMap() },
        restore = {
            if (it.isEmpty()) {
                BackupState()
            } else {
                val stage = BackupStage.values[it[KEY_STAGE] as Int]
                BackupState(stage)
            }
        }
    )

private fun BackupState.toSaverMap() = buildMap {
    put(KEY_STAGE, current.value.order)
}
