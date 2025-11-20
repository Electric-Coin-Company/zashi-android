package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.deletewallet.ResetZashiArgs

class NavigateToResetWalletUseCase(
    private val navigationRouter: NavigationRouter,
) {
    operator fun invoke() {
        navigationRouter.forward(ResetZashiArgs)
    }
}
