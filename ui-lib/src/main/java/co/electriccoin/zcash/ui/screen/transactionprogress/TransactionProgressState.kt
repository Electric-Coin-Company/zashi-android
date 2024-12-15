package co.electriccoin.zcash.ui.screen.transactionprogress

import co.electriccoin.zcash.ui.design.util.StringResource

sealed interface TransactionProgressState {
    val onBack: () -> Unit
}

data class SendingTransactionState(
    val text: StringResource,
    override val onBack: () -> Unit
) : TransactionProgressState

data class SuccessfulTransactionState(
    val text: StringResource,
    val onViewTransactionClick: () -> Unit,
    val onCloseClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState

data class FailureTransactionState(
    val onViewTransactionClick: () -> Unit,
    val onCloseClick: () -> Unit,
    val onReportClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState

data class GrpcFailureTransactionState(
    val onCloseClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState

data class MultipleFailuresTransactionState(
    val showBackButton: Boolean,
    val transactionIds: List<String>,
    val onCopyClick: () -> Unit,
    val onSupportClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState
