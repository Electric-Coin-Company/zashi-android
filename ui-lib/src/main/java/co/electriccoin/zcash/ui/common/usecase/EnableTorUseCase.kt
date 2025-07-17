package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class EnableTorUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(enable: Boolean) {
        walletRepository.enableTor(enable)
        navigationRouter.back()
    }
}
