package co.electriccoin.zcash.ui.common.provider

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update

interface ApplicationStateProvider {
    val state: StateFlow<Lifecycle.Event?>

    suspend fun getApplicationState(): Lifecycle.Event?

    fun setApplicationState(newState: Lifecycle.Event)
}

class ApplicationStateProviderImpl : ApplicationStateProvider {
    private val _state = MutableStateFlow<Lifecycle.Event?>(null)

    override val state = _state.asStateFlow()

    override suspend fun getApplicationState(): Lifecycle.Event? = _state.last()

    override fun setApplicationState(newState: Lifecycle.Event) {
        _state.update { newState }
    }
}

fun Lifecycle.Event?.isInForeground(): Boolean =
    this == Lifecycle.Event.ON_RESUME ||
        this == Lifecycle.Event.ON_START
