package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import java.math.BigDecimal

class SetSlippageUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter
) {
    operator fun invoke(slippage: BigDecimal) {
        swapRepository.setSlippage(slippage)
        navigationRouter.back()
    }
}
