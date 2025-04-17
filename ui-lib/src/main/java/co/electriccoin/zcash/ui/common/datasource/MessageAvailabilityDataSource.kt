package co.electriccoin.zcash.ui.common.datasource

import androidx.lifecycle.Lifecycle
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

interface MessageAvailabilityDataSource {
    val canShowMessage: Boolean
    fun observe(): Flow<Boolean>
    fun onMessageShown()
    fun onThirdPartyUiShown()
}

class MessageAvailabilityDataSourceImpl(
    applicationStateProvider: ApplicationStateProvider
) : MessageAvailabilityDataSource {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val state = MutableStateFlow(
        MessageAvailabilityData(
            isAppInForeground = true,
            isThirdPartyUiShown = false,
            hasMessageBeenShown = false
        )
    )

    override val canShowMessage: Boolean
        get() = state.value.canShowMessage

    init {
        applicationStateProvider.state
            .onEach { event ->
                if (event == Lifecycle.Event.ON_START) {
                    state.update {
                        it.copy(
                            isAppInForeground = true,
                            hasMessageBeenShown = if (it.isThirdPartyUiShown) it.hasMessageBeenShown else false,
                            isThirdPartyUiShown = false
                        )
                    }
                } else if (event == Lifecycle.Event.ON_STOP) {
                    state.update {
                        it.copy(
                            isAppInForeground = it.isThirdPartyUiShown,
                        )
                    }
                }
            }
            .launchIn(scope)
    }

    override fun observe(): Flow<Boolean> = state.map { it.canShowMessage }.distinctUntilChanged()

    override fun onMessageShown() {
        state.update { it.copy(hasMessageBeenShown = true) }
    }

    override fun onThirdPartyUiShown() {
        state.update { it.copy(isThirdPartyUiShown = true) }
    }
}

private data class MessageAvailabilityData(
    val isAppInForeground: Boolean,
    val isThirdPartyUiShown: Boolean,
    val hasMessageBeenShown: Boolean
) {
    val canShowMessage = isAppInForeground && !hasMessageBeenShown
}
