package cash.z.ecc.ui.screen.backup.state

import co.electriccoin.zcash.spackle.model.Index
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestChoices(initial: Map<Index, String?> = emptyMap()) {
    private val mutableState = MutableStateFlow<Map<Index, String?>>(HashMap(initial))

    val current: StateFlow<Map<Index, String?>> = mutableState

    fun set(map: Map<Index, String?>) {
        mutableState.value = HashMap(map)
    }
}
