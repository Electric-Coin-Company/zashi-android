package co.electriccoin.zcash.ui.common.provider

import android.content.Context
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes

interface BlockchainProvider {
    fun getBlockchain(ticker: String): SwapAssetBlockchain

    fun getHardcodedBlockchains(): List<SwapAssetBlockchain>
}

class BlockchainProviderImpl(
    private val context: Context
): BlockchainProvider {
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
                "avax" -> stringRes("Avalanche")
                "bsc" -> stringRes("BNB Chain")
                "op" -> stringRes("Optimism")
                "pol" -> stringRes("Polygon")
                "ton" -> stringRes("TON")
                "sui" -> stringRes("SUI")
                else -> stringRes(ticker)
            },
            chainIcon = getChainIcon(ticker)
        )
    }

    private fun getChainIcon(ticker: String): ImageResource {
        val id = context.resources.getIdentifier(
            "ic_chain_${ticker.lowercase()}",
            "drawable",
            context.packageName
        )

        return if (id == 0) imageRes(R.drawable.ic_chain_placeholder) else imageRes(id)
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
