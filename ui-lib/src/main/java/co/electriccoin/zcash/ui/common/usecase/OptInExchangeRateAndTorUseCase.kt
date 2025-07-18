package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.screen.advancedsettings.AdvancedSettingsArgs

class OptInExchangeRateAndTorUseCase(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val navigationRouter: NavigationRouter,
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() {
        exchangeRateRepository.optInExchangeRateUsd(true)
        walletRepository.enableTor(true)
        navigationRouter.backTo(AdvancedSettingsArgs::class)
    }
}