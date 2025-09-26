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

    fun getZcashBlockchain(): SwapAssetBlockchain
}

class BlockchainProviderImpl(
    private val context: Context
) : BlockchainProvider {
    @Suppress("CyclomaticComplexMethod")
    override fun getBlockchain(ticker: String): SwapAssetBlockchain =
        SwapAssetBlockchain(
            chainTicker = ticker,
            chainName =
                when (ticker.lowercase()) {
                    "aptos" -> stringRes("Aptos")
                    "arb" -> stringRes("Arbitrum")
                    "avax" -> stringRes("Avalanche")
                    "base" -> stringRes("Base")
                    "bera" -> stringRes("Bera")
                    "bsc" -> stringRes("BNB Chain")
                    "btc" -> stringRes("Bitcoin")
                    "cardano" -> stringRes("Cardano")
                    "doge" -> stringRes("Doge")
                    "eth" -> stringRes("Ethereum")
                    "gnosis" -> stringRes("Gnosis")
                    "near" -> stringRes("Near")
                    "op" -> stringRes("Optimism")
                    "pol" -> stringRes("Polygon")
                    "sol" -> stringRes("Solana")
                    "stellar" -> stringRes("Stellar")
                    "sui" -> stringRes("SUI")
                    "ton" -> stringRes("TON")
                    "tron" -> stringRes("Tron")
                    "xrp" -> stringRes("Ripple")
                    "zec" -> stringRes("ZEC")
                    else -> stringRes(ticker)
                },
            chainIcon = getChainIcon(ticker)
        )

    private fun getChainIcon(ticker: String): ImageResource {
        val id =
            context.resources.getIdentifier(
                "ic_chain_${ticker.lowercase()}",
                "drawable",
                context.packageName
            )

        return if (id == 0) imageRes(R.drawable.ic_chain_placeholder) else imageRes(id)
    }

    override fun getHardcodedBlockchains(): List<SwapAssetBlockchain> =
        listOf(
            "aptos",
            "arb",
            "avax",
            "base",
            "bera",
            "bsc",
            "btc",
            "cardano",
            "doge",
            "eth",
            "gnosis",
            "near",
            "op",
            "pol",
            "sol",
            "stellar",
            "sui",
            "ton",
            "tron",
            "xrp",
        ).map { getBlockchain(it) }

    override fun getZcashBlockchain(): SwapAssetBlockchain = getBlockchain("zec")
}
