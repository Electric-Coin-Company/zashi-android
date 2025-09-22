package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.imageRes
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
    val chainIcon: ImageResource
        get() = blockchain.chainIcon
}

data class ZecSwapAsset(
    override val tokenTicker: String,
    override val tokenName: StringResource,
    override val tokenIcon: ImageResource,
    override val usdPrice: BigDecimal?,
    override val assetId: String,
    override val decimals: Int,
    override val blockchain: SwapAssetBlockchain,
) : SwapAsset {
    val alternativeTokenIcon: ImageResource = imageRes(R.drawable.ic_zec_round_full)
}

fun SwapAsset.getQuoteTokenIcon(): ImageResource = when (this) {
    is DynamicSwapAsset -> this.tokenIcon
    is ZecSwapAsset -> this.alternativeTokenIcon
}

fun SwapAsset.getQuoteChainIcon(
    isOriginAsset: Boolean
): ImageResource? = when (this) {
    is DynamicSwapAsset -> this.chainIcon
    is ZecSwapAsset -> if (isOriginAsset) {
        imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield)
    } else {
        imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_unshielded)
    }
}

data class DynamicSwapAsset(
    override val tokenTicker: String,
    override val tokenName: StringResource,
    override val tokenIcon: ImageResource,
    override val usdPrice: BigDecimal?,
    override val assetId: String,
    override val decimals: Int,
    override val blockchain: SwapAssetBlockchain,
) : SwapAsset

data class SwapAssetBlockchain(
    val chainTicker: String,
    val chainName: StringResource,
    val chainIcon: ImageResource,
)
