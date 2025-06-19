package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.swap.near.NearInfoArgs

class NavigateToSwapInfoUseCase(
    private val navigationRouter: NavigationRouter
) {
    operator fun invoke() {
        navigationRouter.forward(NearInfoArgs)
    }
}
