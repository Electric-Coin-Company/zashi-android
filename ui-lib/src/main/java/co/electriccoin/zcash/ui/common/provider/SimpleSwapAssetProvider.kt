package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.SimpleSwapAsset

interface SimpleSwapAssetProvider {
    fun getSimpleAsset(tokenTicker: String, chainTicker: String): SimpleSwapAsset
}

class SimpleSwapAssetProviderImpl(
    private val tokenIconProvider: TokenIconProvider,
    private val tokenNameProvider: TokenNameProvider,
    private val blockchainProvider: BlockchainProvider,
) : SimpleSwapAssetProvider {
    override fun getSimpleAsset(tokenTicker: String, chainTicker: String): SimpleSwapAsset =
        SimpleSwapAsset(
            tokenName = tokenNameProvider.getName(tokenTicker),
            tokenIcon = tokenIconProvider.getIcon(tokenTicker),
            blockchain = blockchainProvider.getBlockchain(chainTicker),
            tokenTicker = tokenTicker,
        )
}