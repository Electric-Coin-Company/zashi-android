package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapRepository

class SelectSwapAssetUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter,
) {
    fun select(swapAsset: SwapAsset) {
        swapRepository.select(swapAsset)
        navigationRouter.back()
    }
}
