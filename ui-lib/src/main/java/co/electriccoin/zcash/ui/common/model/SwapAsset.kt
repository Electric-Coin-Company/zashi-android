package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import java.math.BigDecimal

data class SwapAsset(
    val tokenTicker: String,
    val tokenName: StringResource,
    val tokenIcon: ImageResource,
    val usdPrice: BigDecimal?,
    val assetId: String,
    val decimals: Int,
    val blockchain: SwapAssetBlockchain,
) {
    val chainTicker: String
        get() = blockchain.chainTicker
    val chainName: StringResource
        get() = blockchain.chainName
    val chainIcon: ImageResource
        get() = blockchain.chainIcon
}

data class SwapAssetBlockchain(
    val chainTicker: String,
    val chainName: StringResource,
    val chainIcon: ImageResource,
)
