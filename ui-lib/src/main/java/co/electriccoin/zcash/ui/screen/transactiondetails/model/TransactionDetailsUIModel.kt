package co.electriccoin.zcash.ui.screen.transactiondetails.model

import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.ZcashNetwork

data class TransactionDetailsUIModel(
    val transactionOverview: TransactionOverview,
    val transactionRecipient: TransactionRecipient,
    val network: ZcashNetwork,
    val networkHeight: BlockHeight?,
    val memo: String = ""
)
