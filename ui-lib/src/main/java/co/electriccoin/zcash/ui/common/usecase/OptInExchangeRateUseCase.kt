package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository

class OptInExchangeRateUseCase(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val navigationRouter: NavigationRouter
) {
    operator fun invoke(optIn: Boolean) {
        if (optIn) optIn() else optOut()
    }

    private fun optOut() {
        exchangeRateRepository.optInExchangeRateUsd(false)
        navigationRouter.back()
    }

    private fun optIn() {
        exchangeRateRepository.optInExchangeRateUsd(true)
        navigationRouter.back()
    }
}
