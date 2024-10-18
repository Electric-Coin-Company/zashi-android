package co.electriccoin.zcash.ui.screen.paymentrequest.model

import androidx.lifecycle.SavedStateHandle
import co.electriccoin.zcash.ui.NavigationArguments
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import kotlinx.serialization.json.Json

data class PaymentRequestArguments(
    val address: SerializableAddress?,
    val amount: Long?,
    val memo: String?,
    val proposal: ByteArray?,
    val zip321Uri: String?,
) {
    companion object {
        internal fun fromSavedStateHandle(savedStateHandle: SavedStateHandle) =
            PaymentRequestArguments(
                address =
                    savedStateHandle.get<String>(NavigationArguments.PAYMENT_REQUEST_RECIPIENT_ADDRESS)?.let {
                        Json.decodeFromString<SerializableAddress>(it)
                    },
                amount = savedStateHandle.get<Long>(NavigationArguments.PAYMENT_REQUEST_AMOUNT),
                memo = savedStateHandle.get<String>(NavigationArguments.PAYMENT_REQUEST_MEMO),
                proposal = savedStateHandle.get<ByteArray>(NavigationArguments.PAYMENT_REQUEST_PROPOSAL),
                zip321Uri = savedStateHandle.get<String>(NavigationArguments.PAYMENT_REQUEST_URI)
            ).also {
                // Remove arguments passed from the other screen if some exist
                savedStateHandle.remove<String>(NavigationArguments.PAYMENT_REQUEST_RECIPIENT_ADDRESS)
                savedStateHandle.remove<Long>(NavigationArguments.PAYMENT_REQUEST_AMOUNT)
                savedStateHandle.remove<String>(NavigationArguments.PAYMENT_REQUEST_MEMO)
                savedStateHandle.remove<ByteArray>(NavigationArguments.PAYMENT_REQUEST_PROPOSAL)
                savedStateHandle.remove<String>(NavigationArguments.PAYMENT_REQUEST_URI)
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentRequestArguments

        if (address != other.address) return false
        if (amount != other.amount) return false
        if (memo != other.memo) return false
        if (proposal != null) {
            if (other.proposal == null) return false
            if (!proposal.contentEquals(other.proposal)) return false
        } else if (other.proposal != null) return false
        if (zip321Uri != other.zip321Uri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address?.hashCode() ?: 0
        result = 31 * result + (amount?.hashCode() ?: 0)
        result = 31 * result + (memo?.hashCode() ?: 0)
        result = 31 * result + (proposal?.contentHashCode() ?: 0)
        result = 31 * result + (zip321Uri?.hashCode() ?: 0)
        return result
    }
}
