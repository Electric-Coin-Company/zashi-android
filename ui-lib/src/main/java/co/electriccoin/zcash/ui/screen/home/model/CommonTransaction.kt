package co.electriccoin.zcash.ui.screen.home.model

import cash.z.ecc.android.sdk.model.PendingTransaction
import cash.z.ecc.android.sdk.model.TransactionOverview

/**
 * A common transactions wrapper class to provide unified way to work with a transactions classes from our SDK.
 */
sealed class CommonTransaction {
    data class Pending(val data: PendingTransaction) : CommonTransaction()
    data class Overview(val data: TransactionOverview) : CommonTransaction()
}
