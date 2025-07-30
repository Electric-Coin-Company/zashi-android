package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.model.near.NearTokenDto
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import java.math.BigDecimal

sealed interface SwapAsset {
    val tokenTicker: String
    val tokenName: StringResource
    val tokenIcon: ImageResource
    val usdPrice: BigDecimal?
    val assetId: String
    val decimals: Int
    val blockchain: SwapAssetBlockchain

    val chainTicker: String
        get() = blockchain.chainTicker
    val chainName: StringResource
        get() = blockchain.chainName
    val chainIcon: ImageResource?
        get() = blockchain.chainIcon
}

data class SwapAssetBlockchain(
    val chainTicker: String,
    val chainName: StringResource,
    val chainIcon: ImageResource?,
)

data class NearSwapAsset(
    val token: NearTokenDto,
    override val tokenName: StringResource,
    override val tokenIcon: ImageResource,
    override val blockchain: SwapAssetBlockchain
) : SwapAsset {
    override val tokenTicker: String = token.symbol
    override val chainTicker: String = token.blockchain
    override val usdPrice: BigDecimal? = token.price
    override val assetId: String = token.assetId
    override val decimals: Int = token.decimals
}
