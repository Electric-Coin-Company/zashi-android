package co.electriccoin.zcash.ui.screen.transactiondetail.info

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.util.StringResource

sealed interface TransactionDetailInfoState

@Immutable
data class SendShieldedState(
    val contact: StringResource?,
    val address: StringResource,
    val addressAbbreviated: StringResource,
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val onTransactionAddressClick: () -> Unit,
    val fee: StringResource,
    val completedTimestamp: StringResource,
    val memo: TransactionDetailMemosState
) : TransactionDetailInfoState

@Immutable
data class SendTransparentState(
    val contact: StringResource?,
    val address: StringResource,
    val addressAbbreviated: StringResource,
    val transactionId: StringResource,
    val onTransactionIdClick: () -> Unit,
    val onTransactionAddressClick: () -> Unit,
    val fee: StringResource,
    val completedTimestamp: StringResource,
) : TransactionDetailInfoState

@Immutable
data class ReceiveShieldedState(
    val memo: TransactionDetailMemosState,
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
data class TransactionDetailMemosState(
    val memos: List<TransactionDetailMemoState>,
)

@Immutable
data class TransactionDetailMemoState(
    val content: StringResource,
    val onClick: () -> Unit
)
