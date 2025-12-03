package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.provider.IsExchangeRateEnabledStorageProvider
import co.electriccoin.zcash.ui.common.provider.IsTorEnabledStorageProvider

class OptInExchangeRateAndTorUseCase(
    private val navigationRouter: NavigationRouter,
    private val isExchangeRateEnabledStorageProvider: IsExchangeRateEnabledStorageProvider,
    private val isTorEnabledStorageProvider: IsTorEnabledStorageProvider
) {
    suspend operator fun invoke(optIn: Boolean) {
        isTorEnabledStorageProvider.store(optIn)
        if (VersionInfo.IS_CMC_AVAILABLE && !optIn) {
            isExchangeRateEnabledStorageProvider.store(false)
        }
        navigationRouter.back()
    }
}
