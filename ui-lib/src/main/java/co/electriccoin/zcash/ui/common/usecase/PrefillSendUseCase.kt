package co.electriccoin.zcash.ui.common.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PrefillSendUseCase {
    private val bus = Channel<DetailedTransactionData>()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    operator fun invoke() = bus.receiveAsFlow()

    fun request(value: DetailedTransactionData) =
        scope.launch {
            bus.send(value)
        }
}
