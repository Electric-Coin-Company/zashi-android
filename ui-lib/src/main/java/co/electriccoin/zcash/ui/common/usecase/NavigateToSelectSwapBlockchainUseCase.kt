package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.screen.swap.picker.SwapBlockchainPickerArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class NavigateToSelectSwapBlockchainUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val pipeline = MutableSharedFlow<SelectSwapBlockchainPipelineResult>()

    suspend operator fun invoke(): SwapAssetBlockchain? {
        val args = SwapBlockchainPickerArgs()
        navigationRouter.forward(args)
        val result = pipeline.first { it.args.requestId == args.requestId }
        return when (result) {
            is SelectSwapBlockchainPipelineResult.Cancelled -> null
            is SelectSwapBlockchainPipelineResult.Scanned -> result.blockchain
        }
    }

    suspend fun onSelectionCancelled(args: SwapBlockchainPickerArgs) {
        pipeline.emit(SelectSwapBlockchainPipelineResult.Cancelled(args))
        navigationRouter.back()
    }

    suspend fun onSelected(blockchain: SwapAssetBlockchain, args: SwapBlockchainPickerArgs) {
        pipeline.emit(
            SelectSwapBlockchainPipelineResult.Scanned(
                blockchain = blockchain,
                args = args
            )
        )
        navigationRouter.back()
    }
}

private sealed interface SelectSwapBlockchainPipelineResult {

    val args: SwapBlockchainPickerArgs

    data class Cancelled(
        override val args: SwapBlockchainPickerArgs
    ) : SelectSwapBlockchainPipelineResult

    data class Scanned(
        val blockchain: SwapAssetBlockchain,
        override val args: SwapBlockchainPickerArgs
    ) : SelectSwapBlockchainPipelineResult
}
