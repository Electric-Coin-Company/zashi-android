package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.IsSwapOptInEnabledProvider
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.SwapArgs
import co.electriccoin.zcash.ui.screen.swap.optin.SwapOptInArgs

class NavigateToNearSwapUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter,
    private val isSwapOptInEnabledProvider: IsSwapOptInEnabledProvider
) {
    suspend operator fun invoke() {
        if (isSwapOptInEnabledProvider.get()) {
            navigationRouter.forward(SwapOptInArgs)
        } else {
            swapRepository.requestRefreshAssets()
            navigationRouter.forward(SwapArgs)
        }
    }
}
