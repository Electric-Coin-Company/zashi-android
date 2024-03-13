package co.electriccoin.zcash.ui.screen.account.state

import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient

data class TransactionOverviewExt(
    val overview: TransactionOverview,
    val recipient: TransactionRecipient?
)
