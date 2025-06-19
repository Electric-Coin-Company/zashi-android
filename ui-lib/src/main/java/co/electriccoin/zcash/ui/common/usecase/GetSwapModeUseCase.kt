package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.SwapRepository

class GetSwapModeUseCase(
    private val swapRepository: SwapRepository
) {
    fun observe() = swapRepository.mode
}
