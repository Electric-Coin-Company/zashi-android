package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.provider.BlockchainProvider
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.design.util.getString

class FilterSwapBlockchainsUseCase(
    private val context: Context,
    private val blockchainProvider: BlockchainProvider,
) {
    operator fun invoke(assets: SwapAssetsData, text: String): SwapBlockchainData {
        val blockchains = assets.data
            ?.map { it.blockchain }
            ?.distinctBy { it.chainTicker } ?: blockchainProvider.getHardcodedBlockchains()
        val sorted = blockchains.sortedBy { it.chainTicker }
        val filtered = buildSet {
            addAll(sorted.filter { it.chainTicker.startsWith(text, ignoreCase = true) })
            addAll(sorted.filter { it.chainTicker.contains(text, ignoreCase = true) })
            addAll(sorted.filter { it.chainName.getString(context).startsWith(text, ignoreCase = true) })
            addAll(sorted.filter { it.chainName.getString(context).contains(text, ignoreCase = true) })
        }.toList()

        return SwapBlockchainData(
            data = filtered,
            isLoading = assets.isLoading,
            type = assets.type
        )
    }
}

data class SwapBlockchainData(
    val data: List<SwapAssetBlockchain>?,
    val isLoading: Boolean,
    val type: SwapAssetsData.Type
)
