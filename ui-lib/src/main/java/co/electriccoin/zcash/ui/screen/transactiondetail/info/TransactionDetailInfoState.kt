package co.electriccoin.zcash.ui.screen.transactiondetail.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

sealed interface TransactionDetailInfoState

@Immutable
data class SendShieldedState(
    val contact: StringResource?,
    val address: StringResource,
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val onTransactionAddressClick: () -> Unit,
    val fee: StringResource,
    val completedTimestamp: StringResource,
    val memo: TransactionDetailMemoState
) : TransactionDetailInfoState

@Immutable
data class SendTransparentState(
    val contact: StringResource?,
    val address: StringResource,
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val onTransactionAddressClick: () -> Unit,
    val fee: StringResource,
    val completedTimestamp: StringResource,
) : TransactionDetailInfoState

@Immutable
data class ReceiveShieldedState(
    val memo: TransactionDetailMemoState,
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val completedTimestamp: StringResource
) : TransactionDetailInfoState

@Immutable
data class ReceiveTransparentState(
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val completedTimestamp: StringResource
) : TransactionDetailInfoState

@Immutable
data class ShieldingState(
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val completedTimestamp: StringResource,
    val fee: StringResource,
) : TransactionDetailInfoState

@Immutable
data class TransactionDetailMemoState(
    val memos: List<StringResource>,
)

@Composable
fun StringResource.abbreviated() = stringRes("${this.getValue().take(ADDRESS_MAX_LENGTH_ABBREVIATED)}...")

private const val ADDRESS_MAX_LENGTH_ABBREVIATED = 20
