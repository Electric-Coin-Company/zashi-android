package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.ExchangeRateOptInStorageProvider
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class EnableTorUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletRepository: WalletRepository,
    private val isExchangeRateOptInStorageProvider: ExchangeRateOptInStorageProvider
) {
    suspend operator fun invoke(enable: Boolean) {
        walletRepository.enableTor(enable)
        if (!enable) isExchangeRateOptInStorageProvider.store(false)
        navigationRouter.back()
    }
}
