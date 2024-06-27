package co.electriccoin.zcash.ui.common.extension

import cash.z.ecc.sdk.extension.ZecAmountPair
import co.electriccoin.zcash.ui.design.component.ZecAmountTriple

fun ZecAmountPair.asZecAmountTriple(prefix: String? = null) =
    ZecAmountTriple(
        main = main,
        suffix = suffix,
        prefix = prefix
    )

fun String.asZecAmountTriple(prefix: String? = null) =
    ZecAmountTriple(
        main = this,
        prefix = prefix
    )
