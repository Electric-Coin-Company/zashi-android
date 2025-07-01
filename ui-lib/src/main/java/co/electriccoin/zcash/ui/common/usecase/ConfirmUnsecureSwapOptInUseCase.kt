package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.IsSwapOptInEnabledProvider
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.screen.swap.SwapArgs
import co.electriccoin.zcash.ui.screen.swap.optin.SwapUnsecureOptInArgs

class ConfirmUnsecureSwapOptInUseCase(
    private val navigationRouter: NavigationRouter,
    private val isSwapOptInEnabledProvider: IsSwapOptInEnabledProvider,
    private val swapRepository: SwapRepository
) {
    suspend operator fun invoke() {
        isSwapOptInEnabledProvider.store(false)
        swapRepository.requestRefreshAssets()
        navigationRouter.replaceAll(SwapArgs)
    }
}