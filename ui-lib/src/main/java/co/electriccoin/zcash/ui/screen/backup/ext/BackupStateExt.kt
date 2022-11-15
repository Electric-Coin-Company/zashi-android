package co.electriccoin.zcash.ui.screen.backup.ext

import androidx.compose.runtime.saveable.mapSaver
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState

private const val KEY_STAGE = "stage" // $NON-NLS
private const val KEY_TEST_STAGE = "test_stage" // $NON-NLS

internal val BackupState.Companion.Saver
    get() = run {
        mapSaver(
            save = { it.toSaverMap() },
            restore = {
                if (it.isEmpty()) {
                    BackupState()
                } else {
                    val stage = BackupStage.values()[it[KEY_STAGE] as Int]
                    val testStage = it[KEY_TEST_STAGE]
                    if (stage is BackupStage.Test) {
                        stage.testStage = testStage as BackupStage.Test.TestStage
                    }
                    BackupState(stage)
                }
            }
        )
    }

private fun BackupState.toSaverMap() = buildMap {
    put(KEY_STAGE, current.value.order)
    if (current.value is BackupStage.Test) {
        put(KEY_TEST_STAGE, (current.value as BackupStage.Test).testStage)
    }
}
