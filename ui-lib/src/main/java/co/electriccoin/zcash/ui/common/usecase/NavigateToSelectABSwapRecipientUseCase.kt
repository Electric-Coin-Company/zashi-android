package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.ab.SelectABSwapRecipientArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class NavigateToSelectABSwapRecipientUseCase(
    private val navigationRouter: NavigationRouter,
    private val swapRepository: SwapRepository
) {
    private val pipeline = MutableSharedFlow<SelectSwapRecipientPipelineResult>()

    suspend operator fun invoke(): EnhancedABContact? {
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

    suspend fun onSelected(contact: EnhancedABContact, args: SelectABSwapRecipientArgs) {

        if (contact.blockchain?.chainTicker?.lowercase()
            != swapRepository.selectedAsset.value?.chainTicker?.lowercase()
        ) {
            getSwapAssetByBlockchainTicker(contact.blockchain)?.let { swapRepository.select(it) }
        }

        pipeline.emit(SelectSwapRecipientPipelineResult.Scanned(contact = contact, args = args))
        navigationRouter.back()
    }

    private fun getSwapAssetByBlockchainTicker(blockchain: SwapAssetBlockchain?): SwapAsset? {
        if (blockchain == null) return null

        val foundAssetsWithChain = swapRepository.assets.value.data
            ?.filter { it.chainTicker.lowercase() == blockchain.chainTicker.lowercase() }
            ?.takeIf { it.isNotEmpty() } ?: return null

        if (foundAssetsWithChain.size == 1) return foundAssetsWithChain.first()

        val foundStableCoin = foundAssetsWithChain.find { it.tokenTicker.lowercase() == "usdc" }
            ?: foundAssetsWithChain.find { it.tokenTicker.lowercase() == "usdt" }
            ?: foundAssetsWithChain.find { it.tokenTicker.lowercase() == "dai" }
            ?: foundAssetsWithChain.find { it.tokenTicker.lowercase() == "lusd" }

        if (foundStableCoin != null) return foundStableCoin

        val foundExplicitNativeToken: SwapAsset? = when (blockchain.chainTicker.lowercase()) {
            "xrp" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "xrp" }
            "eth" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "eth" }
            "arb" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "arb" }
            "bsc" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "bnb" }
            "sol" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "sol" }
            "ada" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "ada" }
            "trx" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "trx" }
            "avax" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "avax" }
            "matic" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "matic" }
            "algo" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "algo" }
            "base" -> foundAssetsWithChain.find { it.tokenTicker.lowercase() == "eth" }
            else -> null
        }

        return foundExplicitNativeToken
            ?: foundAssetsWithChain.find { it.tokenTicker.lowercase() == it.chainTicker.lowercase() }
            ?: foundAssetsWithChain.firstOrNull()
    }
}

private sealed interface SelectSwapRecipientPipelineResult {

    val args: SelectABSwapRecipientArgs

    data class Cancelled(
        override val args: SelectABSwapRecipientArgs
    ) : SelectSwapRecipientPipelineResult

    data class Scanned(
        val contact: EnhancedABContact,
        override val args: SelectABSwapRecipientArgs
    ) : SelectSwapRecipientPipelineResult
}
