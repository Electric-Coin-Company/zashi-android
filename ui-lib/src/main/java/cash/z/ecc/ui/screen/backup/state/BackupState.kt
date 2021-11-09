package cash.z.ecc.ui.screen.backup.state

import cash.z.ecc.ui.screen.backup.model.BackupStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @param initialState Allows restoring the state from a different starting point. This is
 * primarily useful on Android, for automated tests, and for iterative debugging with the Compose
 * layout preview. The default constructor argument is generally fine for other platforms.
 */
class BackupState(initialState: BackupStage = BackupStage.values().first()) {

    private val mutableState = MutableStateFlow(initialState)

    val current: StateFlow<BackupStage> = mutableState

    fun hasNext() = current.value.hasNext()

    fun goNext() {
        mutableState.value = current.value.getNext()
    }

    fun goPrevious() {
        mutableState.value = current.value.getPrevious()
    }

    fun goToSeed() {
        mutableState.value = BackupStage.Seed
    }
}
