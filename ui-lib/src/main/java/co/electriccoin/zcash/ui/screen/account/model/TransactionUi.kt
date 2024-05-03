package co.electriccoin.zcash.ui.screen.account.model

import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.screen.account.ext.TransactionOverviewExt

data class TransactionUi(
    val overview: TransactionOverview,
    val recipient: TransactionRecipient?,
    val recipientAddressType: AddressType?,
    val expandableState: TrxItemState,
    val messages: List<String>?
) {
    companion object {
        fun new(
            data: TransactionOverviewExt,
            expandableState: TrxItemState,
            messages: List<String>?
        ) = TransactionUi(
            overview = data.overview,
            recipient = data.recipient,
            recipientAddressType = data.recipientAddressType,
            expandableState = expandableState,
            messages = messages
        )
    }
}
