package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.BlockHeight
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.resync.date.ResyncBDDateArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import java.util.UUID

class NavigateToEstimateBlockHeightUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val pipeline = MutableSharedFlow<EstimateBlockHeightPipelineResult>()

    suspend operator fun invoke(initialBlockHeight: BlockHeight): BlockHeight? {
        val uuid = UUID.randomUUID().toString()
        val args = ResyncBDDateArgs(uuid = uuid, initialBlockHeight = initialBlockHeight.value)
        navigationRouter.forward(args)
        val result = pipeline.first { it.uuid == uuid }
        return when (result) {
            is EstimateBlockHeightPipelineResult.Cancelled -> null
            is EstimateBlockHeightPipelineResult.Selected -> result.blockHeight
        }
    }

    suspend fun onSelectionCancelled(args: ResyncBDDateArgs) {
        pipeline.emit(EstimateBlockHeightPipelineResult.Cancelled(uuid = args.uuid))
        navigationRouter.back()
    }

    suspend fun onSelected(
        blockHeight: Long,
        args: co.electriccoin.zcash.ui.screen.resync.estimation.ResyncBDEstimationArgs
    ) {
        pipeline.emit(
            EstimateBlockHeightPipelineResult.Selected(
                blockHeight = BlockHeight.new(blockHeight),
                uuid = args.uuid
            )
        )
        navigationRouter.back()
    }
}

private sealed interface EstimateBlockHeightPipelineResult {
    val uuid: String

    data class Cancelled(
        override val uuid: String
    ) : EstimateBlockHeightPipelineResult

    data class Selected(
        val blockHeight: BlockHeight,
        override val uuid: String
    ) : EstimateBlockHeightPipelineResult
}
