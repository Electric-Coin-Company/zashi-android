package co.electriccoin.zcash.ui.screen.account.state

import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient

data class TransactionOverviewExt(
    val overview: TransactionOverview,
    val recipient: TransactionRecipient?
)

fun TransactionOverview.getSortHeight(networkHeight: BlockHeight): BlockHeight {
    // Non-null assertion operator is necessary here as the smart cast to is impossible because `minedHeight` and
    // `expiryHeight` are declared in a different module
    return when {
        minedHeight != null -> minedHeight!!
        (expiryHeight?.value ?: 0) > 0 -> expiryHeight!!
        else -> networkHeight
    }
}
