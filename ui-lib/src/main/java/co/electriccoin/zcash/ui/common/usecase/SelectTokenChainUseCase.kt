package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.NearTokenChain
import co.electriccoin.zcash.ui.common.model.SwapTokenChain
import co.electriccoin.zcash.ui.common.repository.SwapRepository

class SelectTokenChainUseCase(
    private val swapRepository: SwapRepository,
    private val navigationRouter: NavigationRouter,
) {
    fun select(swapTokenChain: SwapTokenChain) {
        if (swapTokenChain is NearTokenChain) {
            swapRepository.selectTokenChain(swapTokenChain)
        }
        navigationRouter.back()
    }
}
