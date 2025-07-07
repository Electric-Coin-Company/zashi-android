package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes

interface BlockchainProvider {
    fun getBlockchain(ticker: String): SwapAssetBlockchain

    fun getHardcodedBlockchains(): List<SwapAssetBlockchain>
}

class BlockchainProviderImpl: BlockchainProvider {
    override fun getBlockchain(ticker: String): SwapAssetBlockchain {
        return SwapAssetBlockchain(
            chainTicker = ticker,
            chainName = when (ticker.lowercase()) {
                "arb" -> stringRes("Arbitrum")
                "base" -> stringRes("Base")
                "bera" -> stringRes("Bera")
                "btc" -> stringRes("Bitcoin")
                "doge" -> stringRes("Doge")
                "eth" -> stringRes("Ethereum")
                "gnosis" -> stringRes("Gnosis")
                "near" -> stringRes("Near")
                "sol" -> stringRes("Solana")
                "tron" -> stringRes("Tron")
                "xrp" -> stringRes("Ripple")
                else -> stringRes(ticker)
            },
            chainIcon = when (ticker.lowercase()) {
                "arb" -> imageRes(R.drawable.ic_chain_arb)
                "base" -> imageRes(R.drawable.ic_chain_base)
                "near" -> imageRes(R.drawable.ic_chain_near)
                "btc" -> imageRes(R.drawable.ic_chain_btc)
                "eth" -> imageRes(R.drawable.ic_chain_eth)
                "sol" -> imageRes(R.drawable.ic_chain_sol)
                else -> null
            }
        )
    }

    override fun getHardcodedBlockchains(): List<SwapAssetBlockchain> {
        return listOf(
            "arb",
            "base",
            "bera",
            "btc",
            "doge",
            "eth",
            "gnosis",
            "near",
            "sol",
            "tron",
            "xrp",
        ).map { getBlockchain(it) }
    }
}
