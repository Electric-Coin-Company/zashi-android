package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes

interface TokenNameProvider {
    fun getName(ticker: String): StringResource
}

class TokenNameProviderImpl : TokenNameProvider {
    override fun getName(ticker: String): StringResource =
        when (ticker.lowercase()) {
            "btc" -> stringRes("Bitcoin")
            "eth" -> stringRes("Ethereum")
            "near" -> stringRes("Near")
            "sol" -> stringRes("Solana")
            "tron" -> stringRes("Tron")
            "xrp" -> stringRes("Ripple")
            "zec" -> stringRes("Zcash")
            else -> stringRes(ticker)
        }
}
