package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.screen.receive.Receive

class NavigateToReceiveUseCase(
    private val navigationRouter: NavigationRouter,
    private val accountDataSource: AccountDataSource
) {
    suspend operator fun invoke() {
        accountDataSource.requestNextShieldedAddress()
        navigationRouter.forward(Receive)
    }
}
