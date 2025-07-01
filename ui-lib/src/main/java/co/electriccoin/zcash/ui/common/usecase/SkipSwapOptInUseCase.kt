package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.swap.optin.SwapUnsecureOptInArgs

class SkipSwapOptInUseCase(
    private val navigationRouter: NavigationRouter,
) {
    operator fun invoke() {
        navigationRouter.forward(SwapUnsecureOptInArgs)
    }
}