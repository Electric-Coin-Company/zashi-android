package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.SwapRepository
import kotlinx.coroutines.flow.map

class GetZecSwapAssetUseCase(
    private val swapRepository: SwapRepository
) {
    fun observe() = swapRepository.assets.map { it.zecAsset }
}
