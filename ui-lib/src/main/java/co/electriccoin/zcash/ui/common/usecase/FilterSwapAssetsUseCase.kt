package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.ui.common.model.SimpleSwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.design.util.getString

class FilterSwapAssetsUseCase(
    private val context: Context
) {
    operator fun invoke(
        assets: SwapAssetsData,
        latestAssets: Set<SimpleSwapAsset>?,
        text: String,
        onlyChainTicker: String?,
    ): SwapAssetsData {
        if (assets.data == null) return assets.copy(data = null)

        val sorted =
            assets.data
                .filter {
                    if (onlyChainTicker == null) true else it.chainTicker.lowercase() == onlyChainTicker.lowercase()
                }.sortedBy { it.tokenTicker.replace("$", "") }
                .reorderByLatestAssets(latestAssets)

        val filtered =
            buildSet {
                addAll(sorted.filter { it.tokenTicker.startsWith(text, ignoreCase = true) })
                addAll(sorted.filter { it.tokenTicker.contains(text, ignoreCase = true) })
                addAll(sorted.filter { it.tokenName.getString(context).startsWith(text, ignoreCase = true) })
                addAll(sorted.filter { it.tokenName.getString(context).contains(text, ignoreCase = true) })
                addAll(sorted.filter { it.chainTicker.startsWith(text, ignoreCase = true) })
                addAll(sorted.filter { it.chainTicker.contains(text, ignoreCase = true) })
                addAll(sorted.filter { it.chainName.getString(context).startsWith(text, ignoreCase = true) })
                addAll(sorted.filter { it.chainName.getString(context).contains(text, ignoreCase = true) })
            }.toList()

        return assets.copy(data = filtered)
    }

    @Suppress("ReturnCount")
    private fun List<SwapAsset>.reorderByLatestAssets(simpleAssets: Set<SimpleSwapAsset>?): List<SwapAsset> {
        if (simpleAssets.isNullOrEmpty()) return this

        val foundSwapAssets =
            simpleAssets
                .mapNotNull { latest ->
                    this.find { asset ->
                        asset.tokenTicker.lowercase() == latest.tokenTicker.lowercase() &&
                            asset.chainTicker.lowercase() == latest.chainTicker.lowercase()
                    }
                }

        if (foundSwapAssets.isEmpty()) return this

        val mutable = this.toMutableList()

        foundSwapAssets.forEachIndexed { index, asset ->
            mutable.move(asset, index)
        }

        return mutable.toList()
    }

    private fun MutableList<SwapAsset>.move(asset: SwapAsset, index: Int) {
        remove(asset)
        add(index, asset)
    }
}
