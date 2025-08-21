package co.electriccoin.zcash.ui.common.provider

import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ApplicationStateProvider {
    val isInForeground: Flow<Boolean>

    fun onThirdPartyUiShown()

    fun onApplicationLifecycleChanged(event: Lifecycle.Event)

    fun observeOnForeground(): Flow<Unit>
}

class ApplicationStateProviderImpl : ApplicationStateProvider {
    private val state = MutableStateFlow(ApplicationState(isAppInForeground = true, isThirdPartyUiShown = false))

    override val isInForeground: Flow<Boolean> = state.map { it.isActuallyInForeground }.distinctUntilChanged()

    override fun onApplicationLifecycleChanged(event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            state.update {
                it.copy(
                    isAppInForeground = true,
                    isThirdPartyUiShown = false,
                )
            }
        } else if (event == Lifecycle.Event.ON_STOP) {
            state.update {
                it.copy(
                    isAppInForeground = it.isThirdPartyUiShown
                )
            }
        }
    }

    override fun observeOnForeground(): Flow<Unit> =
        channelFlow {
            launch {
                var previous = state.value.isActuallyInForeground
                isInForeground.collect { isForeground ->
                    if (isForeground && !previous) {
                        send(Unit)
                    }
                    previous = isForeground
                }
            }
            awaitClose {
                // do nothing
            }
        }

    override fun onThirdPartyUiShown() {
        state.update { it.copy(isThirdPartyUiShown = true) }
    }
}

private data class ApplicationState(
    val isAppInForeground: Boolean,
    val isThirdPartyUiShown: Boolean,
) {
    val isActuallyInForeground = isAppInForeground || isThirdPartyUiShown
}
