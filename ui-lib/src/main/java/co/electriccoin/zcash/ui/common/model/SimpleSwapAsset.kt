package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource

data class SimpleSwapAsset(
    val tokenTicker: String,
    val tokenName: StringResource,
    val tokenIcon: ImageResource,
    val blockchain: SwapAssetBlockchain,
) {
    val chainTicker: String = blockchain.chainTicker
}
