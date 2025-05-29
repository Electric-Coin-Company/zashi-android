package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.SwapRepository

class GetSelectedTokenChainUseCase(
    private val swapRepository: SwapRepository
) {
    fun observe() = swapRepository.selectedTokenChain
}
