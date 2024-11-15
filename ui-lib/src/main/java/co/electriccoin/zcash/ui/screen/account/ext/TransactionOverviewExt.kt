package co.electriccoin.zcash.ui.screen.account.ext

import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.TransactionOutput
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.type.AddressType

data class TransactionOverviewExt(
    val overview: TransactionOverview,
    val recipient: TransactionRecipient?,
    val recipientAddressType: AddressType?,
    val transactionOutputs: List<TransactionOutput>
)

/**
 * This extension provides the best height that can currently be offered.
 *
 * @return It returns a height for the transaction list sorting in this order:
 * [minedHeight] -> [expiryHeight] -> [networkHeight] -> null
 */
fun TransactionOverview.getSortHeight(networkHeight: BlockHeight?): BlockHeight? {
    // Non-null assertion operator is necessary here as the smart cast to is impossible because `minedHeight` and
    // `expiryHeight` are declared in a different module
    return when {
        minedHeight != null -> minedHeight!!
        (expiryHeight?.value ?: 0) > 0 -> expiryHeight!!
        else -> networkHeight
    }
}
