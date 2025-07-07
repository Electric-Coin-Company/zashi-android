package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.ab.SelectABSwapRecipientArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class NavigateToSelectABSwapRecipientUseCase(
    private val navigationRouter: NavigationRouter,
    private val swapRepository: SwapRepository
) {
    private val pipeline = MutableSharedFlow<SelectSwapRecipientPipelineResult>()

    suspend operator fun invoke(): ContactWithSwapAsset? {
        val args = SelectABSwapRecipientArgs()
        navigationRouter.forward(args)
        val result = pipeline.first { it.args.requestId == args.requestId }
        return when (result) {
            is SelectSwapRecipientPipelineResult.Cancelled -> null
            is SelectSwapRecipientPipelineResult.Scanned -> result.contact
        }
    }

    suspend fun onSelectionCancelled(args: SelectABSwapRecipientArgs) {
        pipeline.emit(SelectSwapRecipientPipelineResult.Cancelled(args))
        navigationRouter.back()
    }

    suspend fun onSelected(contact: ContactWithSwapAsset, args: SelectABSwapRecipientArgs) {
        when (contact.asset.blockchain.chainTicker.lowercase()) {
            "btc" -> getSwapAssetByBlockchainTicker("btc")?.let { swapRepository.select(it) }
            "doge" -> getSwapAssetByBlockchainTicker("doge")?.let { swapRepository.select(it) }
            "xrp" -> getSwapAssetByBlockchainTicker("doge")?.let { swapRepository.select(it) }
            else -> swapRepository.select(null)
        }
        pipeline.emit(SelectSwapRecipientPipelineResult.Scanned(contact = contact, args = args))
        navigationRouter.back()
    }

    private fun getSwapAssetByBlockchainTicker(ticker: String) = swapRepository
        .assets.value.data?.firstOrNull { it.chainTicker.lowercase() == ticker }
}

private sealed interface SelectSwapRecipientPipelineResult {

    val args: SelectABSwapRecipientArgs

    data class Cancelled(
        override val args: SelectABSwapRecipientArgs
    ) : SelectSwapRecipientPipelineResult

    data class Scanned(
        val contact: ContactWithSwapAsset,
        override val args: SelectABSwapRecipientArgs
    ) : SelectSwapRecipientPipelineResult
}
