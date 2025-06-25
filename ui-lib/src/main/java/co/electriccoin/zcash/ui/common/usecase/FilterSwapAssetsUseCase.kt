package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.design.util.getString

class FilterSwapAssetsUseCase(
    private val context: Context
) {
    operator fun invoke(assets: SwapAssetsData, text: String): SwapAssetsData {
        val filtered =
            if (assets.data == null) {
                null
            } else {
                val sorted = assets.data.sortedBy { it.tokenTicker }
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
            }

        return assets.copy(data = filtered)
    }
}
