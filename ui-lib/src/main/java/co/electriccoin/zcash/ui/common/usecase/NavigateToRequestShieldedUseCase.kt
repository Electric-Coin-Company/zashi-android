package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.screen.receive.ReceiveAddressType

class NavigateToRequestShieldedUseCase(
    private val navigationRouter: NavigationRouter,
    private val accountDataSource: AccountDataSource
) {
    suspend operator fun invoke(requestNewAddress: Boolean = true) {
        if (requestNewAddress) {
            accountDataSource.requestNextShieldedAddress()
        }
        navigationRouter.forward("${NavigationTargets.REQUEST}/${ReceiveAddressType.Unified.ordinal}")
    }
}
