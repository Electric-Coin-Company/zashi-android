package co.electriccoin.zcash.ui.common.datasource

import androidx.lifecycle.Lifecycle
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface MessageAvailabilityDataSource {
    val canShowMessage: Boolean
    fun observe(): StateFlow<Boolean>
    fun onMessageShown()
}

class MessageAvailabilityDataSourceImpl(
    private val applicationStateProvider: ApplicationStateProvider
): MessageAvailabilityDataSource {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val state = MutableStateFlow(true)

    override val canShowMessage: Boolean
        get() = state.value

    init {
        scope.launch {
            applicationStateProvider.state.collect {
                if (it == Lifecycle.Event.ON_START) {
                    state.update { true }
                }
            }
        }
    }

    override fun observe(): StateFlow<Boolean> = state.asStateFlow()

    override fun onMessageShown() {
        state.update { false }
    }
}
