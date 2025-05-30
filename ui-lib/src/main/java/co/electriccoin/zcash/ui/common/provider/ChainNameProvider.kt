package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes

interface ChainNameProvider {
    fun getName(ticker: String): StringResource
}

class ChainNameProviderImpl : ChainNameProvider {
    override fun getName(ticker: String): StringResource =
        when (ticker.lowercase()) {
            "arb" -> stringRes("Arbitrum")
            "base" -> stringRes("Base")
            "bera" -> stringRes("Bera")
            "btc" -> stringRes("Bitcoin")
            "eth" -> stringRes("Ethereum")
            "gnosis" -> stringRes("Gnosis")
            "near" -> stringRes("Near")
            "sol" -> stringRes("Solana")
            "tron" -> stringRes("Tron")
            "xrp" -> stringRes("Ripple")
            "zec" -> stringRes("Zcash")
            else -> stringRes(ticker)
        }
}
