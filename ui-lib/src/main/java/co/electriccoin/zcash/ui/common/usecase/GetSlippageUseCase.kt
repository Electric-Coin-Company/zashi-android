package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.SwapRepository

class GetSlippageUseCase(
    private val swapRepository: SwapRepository
) {
    operator fun invoke() = swapRepository.slippage.value

    fun observe() = swapRepository.slippage
}
