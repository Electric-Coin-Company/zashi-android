package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource

data class SwapBlockchain(
    val chainTicker: String,
    val chainName: StringResource,
    val chainIcon: ImageResource,
)
