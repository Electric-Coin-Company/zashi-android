package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.PersistableWalletTorProvider
import co.electriccoin.zcash.ui.common.provider.TorState
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.screen.exchangerate.settings.ExchangeRateTorSettingsArgs

class OptInExchangeRateUseCase(
    private val persistableWalletTorProvider: PersistableWalletTorProvider,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(optIn: Boolean) {
        if (optIn) optIn() else optOut()
    }

    private fun optOut() {
        exchangeRateRepository.optInExchangeRateUsd(false)
        navigationRouter.back()
    }

    private suspend fun optIn() {
        val torState = persistableWalletTorProvider.get()
        if (torState in listOf(TorState.EXPLICITLY_DISABLED, TorState.IMPLICITLY_DISABLED)) {
            navigationRouter.forward(ExchangeRateTorSettingsArgs)
        } else {
            exchangeRateRepository.optInExchangeRateUsd(true)
            navigationRouter.back()
        }
    }
}