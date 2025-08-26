package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
sealed interface TransactionProgressState {
    val onBack: () -> Unit
}

@Immutable
data class SendingTransactionState(
    val title: StringResource,
    val text: StringResource,
    override val onBack: () -> Unit
) : TransactionProgressState

@Immutable
data class SuccessfulTransactionState(
    val title: StringResource,
    val text: StringResource,
    val middleButton: ButtonState?,
    val primaryButton: ButtonState,
    val secondaryButton: ButtonState?,
    override val onBack: () -> Unit
) : TransactionProgressState

@Immutable
data class FailureTransactionState(
    val title: StringResource,
    val text: StringResource,
    val onViewTransactionClick: () -> Unit,
    val onCloseClick: () -> Unit,
    val onReportClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState

@Immutable
data class GrpcFailureTransactionState(
    val onCloseClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState

@Immutable
data class MultipleFailuresTransactionState(
    val showBackButton: Boolean,
    val transactionIds: List<String>,
    val onCopyClick: () -> Unit,
    val onSupportClick: () -> Unit,
    override val onBack: () -> Unit
) : TransactionProgressState
