package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.SwapRepository

class GetTokenChainsUseCase(
    private val swapRepository: SwapRepository
) {
    fun observe() = swapRepository.tokenChains
}
