package co.electriccoin.zcash.ui.screen.sendconfirmation.model

import androidx.lifecycle.SavedStateHandle
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.NavigationArguments
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import kotlinx.serialization.json.Json

data class SendConfirmationArgsWrapper(
    val address: SerializableAddress?,
    val amount: Long?,
    val memo: String?,
    val proposal: ByteArray?,
) {
    companion object {
        internal fun fromSavedStateHandle(savedStateHandle: SavedStateHandle) =
            SendConfirmationArgsWrapper(
                address =
                    savedStateHandle.get<String>(NavigationArguments.SEND_CONFIRM_RECIPIENT_ADDRESS)?.let {
                        Json.decodeFromString<SerializableAddress>(it)
                    },
                amount = savedStateHandle.get<Long>(NavigationArguments.SEND_CONFIRM_AMOUNT),
                memo = savedStateHandle.get<String>(NavigationArguments.SEND_CONFIRM_MEMO),
                proposal = savedStateHandle.get<ByteArray>(NavigationArguments.SEND_CONFIRM_PROPOSAL),
            )
    }

    internal fun toZecSend() =
        ZecSend(
            destination = address?.toWalletAddress() ?: error("Address null"),
            amount = amount?.let { Zatoshi(amount) } ?: error("Amount null"),
            memo = memo?.let { Memo(memo) } ?: error("Memo null"),
            proposal = proposal?.let { Proposal.fromByteArray(proposal) } ?: error("Proposal null"),
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SendConfirmationArgsWrapper

        if (amount != other.amount) return false
        if (memo != other.memo) return false
        if (address != other.address) return false
        if (proposal != null) {
            if (other.proposal == null) return false
            if (!proposal.contentEquals(other.proposal)) return false
        } else if (other.proposal != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = amount?.hashCode() ?: 0
        result = 31 * result + (memo?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (proposal?.contentHashCode() ?: 0)
        return result
    }
}
