package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapRepository

class UpdateSwapModeUseCase(
    private val swapRepository: SwapRepository
) {
    operator fun invoke(mode: SwapMode) = swapRepository.changeMode(mode)
}
