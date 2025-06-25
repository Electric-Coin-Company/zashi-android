package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.model.near.NearTokenDto
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import java.math.BigDecimal

sealed interface SwapAsset {
    val tokenTicker: String
    val chainTicker: String
    val tokenName: StringResource
    val tokenIcon: ImageResource?
    val chainName: StringResource
    val chainIcon: ImageResource?
    val usdPrice: BigDecimal?
    val assetId: String
}

data class NearSwapAsset(
    val token: NearTokenDto,
    override val tokenName: StringResource,
    override val tokenIcon: ImageResource?,
    override val chainName: StringResource,
    override val chainIcon: ImageResource?,
) : SwapAsset {
    override val tokenTicker: String = token.symbol
    override val chainTicker: String = token.blockchain
    override val usdPrice: BigDecimal? = token.price
    override val assetId: String = token.assetId
}
