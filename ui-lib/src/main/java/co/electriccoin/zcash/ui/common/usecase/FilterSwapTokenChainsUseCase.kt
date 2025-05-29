package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.ui.common.repository.SwapTokenChains
import co.electriccoin.zcash.ui.design.util.getString

class FilterSwapTokenChainsUseCase(
    private val context: Context
) {
    operator fun invoke(tokenChains: SwapTokenChains, text: String): SwapTokenChains {
        val filtered =
            if (tokenChains.data == null) {
                null
            } else {
                val sorted = tokenChains.data.sortedBy { it.tokenTicker }
                buildSet {
                    addAll(sorted.filter { it.tokenTicker.startsWith(text, ignoreCase = true) })
                    addAll(sorted.filter { it.tokenName.getString(context).contains(text, ignoreCase = true) })
                    addAll(sorted.filter { it.chainTicker.startsWith(text, ignoreCase = true) })
                    addAll(sorted.filter { it.chainName.getString(context).contains(text, ignoreCase = true) })
                }.toList()
            }

        return tokenChains.copy(data = filtered)
    }
}
