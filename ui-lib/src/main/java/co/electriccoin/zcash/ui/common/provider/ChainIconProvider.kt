package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes

interface ChainIconProvider {
    fun getIcon(ticker: String): ImageResource?
}

class ChainIconProviderImpl : ChainIconProvider {
    override fun getIcon(ticker: String): ImageResource? =
        when (ticker.lowercase()) {
            "arb" -> imageRes(R.drawable.ic_chain_arb)
            "base" -> imageRes(R.drawable.ic_chain_base)
            "near" -> imageRes(R.drawable.ic_chain_near)
            "btc" -> imageRes(R.drawable.ic_chain_btc)
            "eth" -> imageRes(R.drawable.ic_chain_eth)
            "sol" -> imageRes(R.drawable.ic_chain_sol)
            "zec" -> imageRes(R.drawable.ic_chain_zec)
            else -> null
        }
}
