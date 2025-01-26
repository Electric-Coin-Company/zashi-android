package co.electriccoin.zcash.ui.common.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ObserveClearSendUseCase {
    private val bus = Channel<Unit>()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    operator fun invoke() = bus.receiveAsFlow()

    fun requestClear() =
        scope.launch {
            bus.send(Unit)
        }
}
