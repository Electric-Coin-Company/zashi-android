package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.repository.SwapRepository

class GetSwapAssetBlockchainUseCase(
    private val swapRepository: SwapRepository
) {
    // fun observe() = swapRepository.assets.map { assets ->
    //     assets.data
    //         ?.map { it.blockchain }
    //         ?.distinct()
    //         ?.sortedBy { it.chainTicker }
    // }

    operator fun invoke(chainTicker: String?): SwapAssetBlockchain? {
        if (chainTicker == null) return null

        return swapRepository.assets.value.data?.firstOrNull { it.chainTicker == chainTicker}?.blockchain
    }
}
