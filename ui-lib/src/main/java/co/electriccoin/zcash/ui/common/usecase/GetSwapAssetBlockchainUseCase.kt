package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.SwapBlockchain
import co.electriccoin.zcash.ui.common.provider.BlockchainProvider
import co.electriccoin.zcash.ui.common.repository.SwapRepository

class GetSwapAssetBlockchainUseCase(
    private val swapRepository: SwapRepository,
    private val blockchainProvider: BlockchainProvider
) {
    operator fun invoke(chainTicker: String?): SwapBlockchain? {
        if (chainTicker == null) return null

        return swapRepository.assets.value.data
            ?.firstOrNull { it.chainTicker == chainTicker }
            ?.blockchain ?: blockchainProvider
            .getBlockchain(chainTicker)
    }
}
