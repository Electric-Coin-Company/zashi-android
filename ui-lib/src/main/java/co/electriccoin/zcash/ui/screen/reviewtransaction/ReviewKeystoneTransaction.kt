package co.electriccoin.zcash.ui.screen.reviewtransaction

import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class ReviewKeystoneTransaction(
    val addressString: String,
    val addressType: AddressType,
    val amountLong: Long,
    val memoString: String?
) {
    val amount: Zatoshi
        get() = Zatoshi(amountLong)

    val memo: Memo?
        get() = memoString?.let { Memo(it) }
}