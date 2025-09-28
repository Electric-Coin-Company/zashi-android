package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.DynamicSwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.ZecSwapAsset
import java.math.BigDecimal

interface SwapAssetProvider {
    fun get(
        tokenTicker: String,
        chainTicker: String,
        usdPrice: BigDecimal?,
        assetId: String,
        decimals: Int,
    ): SwapAsset
}

class SwapAssetProviderImpl(
    private val tokenIconProvider: TokenIconProvider,
    private val tokenNameProvider: TokenNameProvider,
    private val blockchainProvider: BlockchainProvider,
) : SwapAssetProvider {
    override fun get(
        tokenTicker: String,
        chainTicker: String,
        usdPrice: BigDecimal?,
        assetId: String,
        decimals: Int,
    ): SwapAsset = if (tokenTicker.lowercase() == "zec" && chainTicker.lowercase() == "zec") {
        ZecSwapAsset(
            tokenName = tokenNameProvider.getName(tokenTicker),
            tokenIcon = tokenIconProvider.getIcon(tokenTicker),
            blockchain = blockchainProvider.getBlockchain(chainTicker),
            tokenTicker = tokenTicker,
            usdPrice = usdPrice,
            assetId = assetId,
            decimals = decimals,
        )
    } else {
        DynamicSwapAsset(
            tokenName = tokenNameProvider.getName(tokenTicker),
            tokenIcon = tokenIconProvider.getIcon(tokenTicker),
            blockchain = blockchainProvider.getBlockchain(chainTicker),
            tokenTicker = tokenTicker,
            usdPrice = usdPrice,
            assetId = assetId,
            decimals = decimals,
        )
    }
}