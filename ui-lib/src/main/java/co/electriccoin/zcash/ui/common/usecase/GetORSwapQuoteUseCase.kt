package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.SwapQuoteStatusData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class GetORSwapQuoteUseCase(
    private val swapRepository: SwapRepository,
    // private val metadataRepository: MetadataRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(depositAddress: String) =
        channelFlow {
            val requestSwipeReloadPipeline = MutableSharedFlow<Unit>()

            val reloadHandle =
                object : ReloadHandle {
                    override fun requestReload() {
                        launch {
                            requestSwipeReloadPipeline.emit(Unit)
                        }
                    }
                }

            val swapFlow =
                requestSwipeReloadPipeline
                    .onStart { emit(Unit) }
                    .flatMapLatest {
                        swapRepository
                            .observeSwapStatus(depositAddress)
                    }

            swapFlow
                .map { swap ->
                    SwapData(
                        data = swap,
                        handle = reloadHandle
                    )
                }.collect {
                    send(it)
                }

            awaitClose {
                // do nothing
            }
        }.distinctUntilChanged()
}

data class SwapData(
    val data: SwapQuoteStatusData?,
    val handle: ReloadHandle
)
