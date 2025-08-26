package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.SwapArgs

class NavigateToNearSwapUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter,
) {
    operator fun invoke() {
        swapRepository.requestRefreshAssets()
        navigationRouter.forward(SwapArgs)
    }
}
