package co.electriccoin.zcash.ui.common.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ObserveClearSendUseCase {
    private val bus = MutableSharedFlow<Unit>()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    operator fun invoke() = bus

    fun requestClear() =
        scope.launch {
            delay(1.seconds)
            bus.emit(Unit)
        }
}
