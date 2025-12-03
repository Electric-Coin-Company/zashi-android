package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.IsExchangeRateEnabledStorageProvider

class OptInExchangeRateUseCase(
    private val navigationRouter: NavigationRouter,
    private val isExchangeRateEnabledStorageProvider: IsExchangeRateEnabledStorageProvider
) {
    suspend operator fun invoke(optIn: Boolean) {
        if (optIn) optIn() else optOut()
    }

    private suspend fun optOut() {
        isExchangeRateEnabledStorageProvider.store(false)
        navigationRouter.back()
    }

    private suspend fun optIn() {
        isExchangeRateEnabledStorageProvider.store(true)
        navigationRouter.back()
    }
}
