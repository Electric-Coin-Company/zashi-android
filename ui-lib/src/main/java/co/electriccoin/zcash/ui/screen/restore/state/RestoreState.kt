package co.electriccoin.zcash.ui.screen.restore.state

import co.electriccoin.zcash.ui.screen.restore.model.RestoreStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @param initialState Allows restoring the state from a different starting point. This is
 * primarily useful on Android, for automated tests, and for iterative debugging with the Compose
 * layout preview. The default constructor argument is generally fine for other platforms.
 */
class RestoreState(initialState: RestoreStage = RestoreStage.values().first()) {

    private val mutableState = MutableStateFlow(initialState)

    val current: StateFlow<RestoreStage> = mutableState

    fun goNext() {
        mutableState.value = current.value.getNext()
    }

    fun goPrevious() {
        mutableState.value = current.value.getPrevious()
    }
}
