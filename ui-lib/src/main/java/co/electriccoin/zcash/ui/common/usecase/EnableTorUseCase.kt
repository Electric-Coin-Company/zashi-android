package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class EnableTorUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletRepository: WalletRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
) {
    suspend operator fun invoke(enable: Boolean) {
        walletRepository.enableTor(enable)
        if (!enable) exchangeRateRepository.optInExchangeRateUsd(false)
        navigationRouter.back()
    }
}
