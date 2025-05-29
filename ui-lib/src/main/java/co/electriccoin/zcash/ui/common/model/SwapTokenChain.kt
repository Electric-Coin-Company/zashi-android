package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource

sealed interface SwapTokenChain {
    val tokenTicker: String
    val chainTicker: String
    val tokenName: StringResource
    val tokenIcon: ImageResource?
    val chainName: StringResource
    val chainIcon: ImageResource?
}

data class NearTokenChain(
    val token: NearTokenDto,
    override val tokenName: StringResource,
    override val tokenIcon: ImageResource?,
    override val chainName: StringResource,
    override val chainIcon: ImageResource?,
) : SwapTokenChain {
    override val tokenTicker: String = token.symbol
    override val chainTicker: String = token.blockchain
}
