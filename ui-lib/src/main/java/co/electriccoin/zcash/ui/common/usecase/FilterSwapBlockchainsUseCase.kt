package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.design.util.getString

class FilterSwapBlockchainsUseCase(
    private val context: Context
) {
    operator fun invoke(assets: SwapAssetsData, text: String): SwapBlockchainData {
        val filtered =
            if (assets.data == null) {
                null
            } else {
                val sorted = assets.data.map { it.blockchain }.sortedBy { it.chainTicker }
                buildSet {
                    addAll(sorted.filter { it.chainTicker.startsWith(text, ignoreCase = true) })
                    addAll(sorted.filter { it.chainTicker.contains(text, ignoreCase = true) })
                    addAll(sorted.filter { it.chainName.getString(context).startsWith(text, ignoreCase = true) })
                    addAll(sorted.filter { it.chainName.getString(context).contains(text, ignoreCase = true) })
                }.toList()
            }

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
