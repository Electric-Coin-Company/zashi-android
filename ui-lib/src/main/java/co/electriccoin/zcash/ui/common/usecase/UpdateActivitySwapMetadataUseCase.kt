package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UpdateActivitySwapMetadataUseCase(
    private val metadataRepository: MetadataRepository,
) {
    val uiPipeline = MutableSharedFlow<Unit>()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val semaphore = Mutex()

    private val channel = Channel<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isUiPipelineActive = uiPipeline
        .subscriptionCount
        .map { it > 0 }
        .distinctUntilChanged()
    // .mapLatest { isActive ->
    //     if (!isActive) {
    //         delay(5.seconds)
    //         false
    //     }
    //     isActive
    // }

    init {
        scope.launch {
            for (item in channel) {

            }
        }
    }

    operator fun invoke(activity: ActivityData) {
        scope.launch {
            semaphore.withLock {
                val depositAddress = when (activity) {
                    is ActivityData.BySwap -> activity.swap.depositAddress
                    is ActivityData.ByTransaction -> activity.metadata.swapMetadata?.depositAddress
                }

                if (depositAddress != null) {
                    channel.send(depositAddress)
                }
            }
        }
    }
}