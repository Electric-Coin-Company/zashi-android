package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes

interface TokenIconProvider {
    fun getIcon(ticker: String): ImageResource?
}

class TokenIconProviderImpl : TokenIconProvider {
    override fun getIcon(ticker: String): ImageResource? =
        when (ticker.lowercase()) {
            "arb" -> imageRes(R.drawable.ic_token_arb)
            "base" -> imageRes(R.drawable.ic_token_base)
            "btc" -> imageRes(R.drawable.ic_token_btc)
            "eth" -> imageRes(R.drawable.ic_token_eth)
            "gnear" -> imageRes(R.drawable.ic_token_gnear)
            "mpdao" -> imageRes(R.drawable.ic_token_mpdao)
            "sol" -> imageRes(R.drawable.ic_token_sol)
            "usdc" -> imageRes(R.drawable.ic_token_usdc)
            "usdt" -> imageRes(R.drawable.ic_token_usdt)
            "zec" -> imageRes(R.drawable.ic_token_zec)
            else -> null
        }
}
