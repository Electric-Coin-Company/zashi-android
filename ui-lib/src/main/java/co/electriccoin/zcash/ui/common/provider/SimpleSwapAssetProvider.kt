package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.DynamicSimpleSwapAsset
import co.electriccoin.zcash.ui.common.model.SimpleSwapAsset
import co.electriccoin.zcash.ui.common.model.ZecSimpleSwapAsset

interface SimpleSwapAssetProvider {
    fun get(tokenTicker: String, chainTicker: String): SimpleSwapAsset
}

class SimpleSwapAssetProviderImpl(
    private val tokenIconProvider: TokenIconProvider,
    private val tokenNameProvider: TokenNameProvider,
    private val blockchainProvider: BlockchainProvider,
) : SimpleSwapAssetProvider {
    override fun get(tokenTicker: String, chainTicker: String): SimpleSwapAsset =
        if (tokenTicker.lowercase() == "zec" && chainTicker.lowercase() == "zec") {
            ZecSimpleSwapAsset(
                tokenName = tokenNameProvider.getName(tokenTicker),
                tokenIcon = tokenIconProvider.getIcon(tokenTicker),
                blockchain = blockchainProvider.getBlockchain(chainTicker),
                tokenTicker = tokenTicker,
            )
        } else {
            DynamicSimpleSwapAsset(
                tokenName = tokenNameProvider.getName(tokenTicker),
                tokenIcon = tokenIconProvider.getIcon(tokenTicker),
                blockchain = blockchainProvider.getBlockchain(chainTicker),
                tokenTicker = tokenTicker,
            )
        }
}
