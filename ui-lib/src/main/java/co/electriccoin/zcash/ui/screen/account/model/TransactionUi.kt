package co.electriccoin.zcash.ui.screen.account.model

import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt

data class TransactionUi(
    val overview: TransactionOverview,
    val recipient: TransactionRecipient?,
    val expandableState: HistoryItemExpandableState
) {
    companion object {
        fun new(
            data: TransactionOverviewExt,
            expandableState: HistoryItemExpandableState
        ) = TransactionUi(
            overview = data.overview,
            recipient = data.recipient,
            expandableState = expandableState
        )
    }
}
