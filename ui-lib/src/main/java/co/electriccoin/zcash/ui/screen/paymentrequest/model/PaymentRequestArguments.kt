package co.electriccoin.zcash.ui.screen.paymentrequest.model

import androidx.lifecycle.SavedStateHandle
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.NavigationArguments
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import kotlinx.serialization.json.Json

data class PaymentRequestArguments(
    val address: SerializableAddress?,
    val amount: Long?,
    val memo: String?,
    val proposal: FirstClassByteArray?,
    val zip321Uri: String?,
) {
    companion object {
        internal fun fromSavedStateHandle(savedStateHandle: SavedStateHandle) =
            PaymentRequestArguments(
                address =
                    savedStateHandle.get<String>(NavigationArguments.PAYMENT_REQUEST_ADDRESS)?.let {
                        Json.decodeFromString<SerializableAddress>(it)
                    },
                amount = savedStateHandle.get<Long>(NavigationArguments.PAYMENT_REQUEST_AMOUNT),
                memo = savedStateHandle.get<String>(NavigationArguments.PAYMENT_REQUEST_MEMO),
                proposal =
                    savedStateHandle.get<ByteArray>(NavigationArguments.PAYMENT_REQUEST_PROPOSAL)?.let {
                        FirstClassByteArray(it)
                    },
                zip321Uri = savedStateHandle.get<String>(NavigationArguments.PAYMENT_REQUEST_URI),
            ).also {
                // Remove the screen arguments passed from the other screen if some exist
                savedStateHandle.remove<String>(NavigationArguments.PAYMENT_REQUEST_ADDRESS)
                savedStateHandle.remove<Long>(NavigationArguments.PAYMENT_REQUEST_AMOUNT)
                savedStateHandle.remove<String>(NavigationArguments.PAYMENT_REQUEST_MEMO)
                savedStateHandle.remove<ByteArray>(NavigationArguments.PAYMENT_REQUEST_PROPOSAL)
                savedStateHandle.remove<String>(NavigationArguments.PAYMENT_REQUEST_URI)
            }
    }

    internal fun toZecSend() =
        ZecSend(
            destination = address?.toWalletAddress() ?: error("Address null"),
            amount = amount?.let { Zatoshi(amount) } ?: error("Amount null"),
            memo = memo?.let { Memo(memo) } ?: error("Memo null"),
            proposal = proposal?.let { Proposal.fromByteArray(proposal.byteArray) } ?: error("Proposal null"),
        )
}
