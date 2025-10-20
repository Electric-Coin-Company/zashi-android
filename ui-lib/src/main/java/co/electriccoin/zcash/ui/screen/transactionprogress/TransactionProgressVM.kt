package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.ExactInputSwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ExactOutputSwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ShieldTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.SwapTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.repository.SubmitProposalState
import co.electriccoin.zcash.ui.common.usecase.CancelProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.GetProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveTransactionSubmitStateUseCase
import co.electriccoin.zcash.ui.common.usecase.SendEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.ViewTransactionDetailAfterSuccessfulProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ViewTransactionsAfterSuccessfulProposalUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class TransactionProgressVM(
    observeTransactionProposal: ObserveProposalUseCase,
    observeTransactionSubmitState: ObserveTransactionSubmitStateUseCase,
    private val getTransactionProposal: GetProposalUseCase,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val cancelKeystoneProposalFlow: CancelProposalFlowUseCase,
    private val viewTransactionsAfterSuccessfulProposal: ViewTransactionsAfterSuccessfulProposalUseCase,
    private val viewTransactionDetailAfterSuccessfulProposal: ViewTransactionDetailAfterSuccessfulProposalUseCase
) : ViewModel() {
    private val supportContacted = MutableStateFlow(false)

    val state: StateFlow<TransactionProgressState?> =
        combine(
            observeTransactionProposal(),
            observeTransactionSubmitState(),
            supportContacted
        ) { proposal, submitState, supportContacted ->
            when (submitState) {
                null, SubmitProposalState.Submitting -> createSendingTransactionState(proposal)
                is SubmitProposalState.Result ->
                    when (val result = submitState.submitResult) {
                        is SubmitResult.Partial ->
                            createPartialFailureTransactionState(supportContacted, result)

                        is SubmitResult.GrpcFailure ->
                            createGrpcFailureTransactionState(result)

                        is SubmitResult.Failure ->
                            createFailureTransactionState(proposal, result)

                        is SubmitResult.Success -> createSuccessfulTransactionState(proposal, result)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createPartialFailureTransactionState(
        supportContacted: Boolean,
        result: SubmitResult.Partial
    ) = MultipleFailuresTransactionState(
        showBackButton = supportContacted,
        onBack = {
            if (supportContacted) {
                onBackToSendFormAndClear()
            }
            // do nothing
        },
        onCopyClick = {
            copyToClipboardUseCase(
                value = result.txIds.joinToString()
            )
        },
        onSupportClick = {
            viewModelScope.launch {
                sendEmailUseCase(result)
                this@TransactionProgressVM.supportContacted.update { true }
            }
        },
        transactionIds = result.txIds
    )

    private fun createGrpcFailureTransactionState(result: SubmitResult.GrpcFailure) =
        GrpcFailureTransactionState(
            onBack = ::onViewTransactions,
            onCloseClick = ::onViewTransactions,
            onViewTransactionClick = { result.txIds.lastOrNull()?.let { onViewTransactionDetailClick(it) } }
        )

    private suspend fun createSuccessfulTransactionState(
        proposal: TransactionProposal?,
        result: SubmitResult.Success
    ) = SuccessfulTransactionState(
        onBack = ::onViewTransactions,
        middleButton =
            when (proposal) {
                is ExactInputSwapTransactionProposal,
                is ExactOutputSwapTransactionProposal -> null

                else ->
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_success_view_trx),
                        onClick = { onViewTransactionClick(result) }
                    )
            },
        secondaryButton =
            when (proposal) {
                is ExactInputSwapTransactionProposal,
                is ExactOutputSwapTransactionProposal ->
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_success_btn_close),
                        onClick = ::onViewTransactions,
                        style = ButtonStyle.SECONDARY
                    )

                else -> null
            },
        primaryButton =
            when (proposal) {
                is ExactInputSwapTransactionProposal,
                is ExactOutputSwapTransactionProposal ->
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_success_btn_check_status),
                        onClick = { onViewTransactionClick(result) },
                        style = ButtonStyle.PRIMARY
                    )

                else ->
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_success_btn_close),
                        onClick = ::onViewTransactions,
                        style = ButtonStyle.TERTIARY
                    )
            },
        text =
            when (proposal) {
                is ShieldTransactionProposal -> stringRes(R.string.send_confirmation_success_subtitle_transparent)

                is ExactInputSwapTransactionProposal ->
                    stringRes(R.string.send_confirmation_success_swap_subtitle)

                is ExactOutputSwapTransactionProposal ->
                    stringRes(
                        "You successfully initiated a cross-chain payment.\nFollow its status on the " +
                            "transaction screen."
                    )

                else -> stringRes(R.string.send_confirmation_success_subtitle, getAddressAbbreviated())
            },
        title =
            if (proposal is ShieldTransactionProposal) {
                stringRes(R.string.send_confirmation_success_title_transparent)
            } else {
                stringRes(R.string.send_confirmation_success_title)
            }
    )

    private fun onViewTransactionClick(result: SubmitResult.Success) {
        val txId = result.txIds.lastOrNull()
        if (txId == null) onViewTransactions() else onViewTransactionDetailClick(txId)
    }

    private fun createFailureTransactionState(
        proposal: TransactionProposal?,
        result: SubmitResult.Failure,
    ) = FailureTransactionState(
        onBack = ::onBackToSendForm,
        onCloseClick = ::onBackToSendForm,
        onViewTransactionClick = { result.txIds.lastOrNull()?.let { onViewTransactionDetailClick(it) } },
        onReportClick = {
            viewModelScope.launch {
                sendEmailUseCase(result)
                this@TransactionProgressVM.supportContacted.update { true }
            }
        },
        title =
            if (proposal is ShieldTransactionProposal) {
                stringRes(R.string.send_confirmation_failure_title_transparent)
            } else {
                stringRes(R.string.send_confirmation_failure_title)
            },
        text =
            when (proposal) {
                is ExactInputSwapTransactionProposal ->
                    stringRes(R.string.send_confirmation_error_swap_subtitle)

                is ExactOutputSwapTransactionProposal ->
                    stringRes(
                        "There was an error initiating a cross-chain payment.\nTry it again, please."
                    )

                is ShieldTransactionProposal -> stringRes(R.string.send_confirmation_failure_subtitle_transparent)
                else -> stringRes(R.string.send_confirmation_failure_subtitle)
            }
    )

    private suspend fun createSendingTransactionState(proposal: TransactionProposal?) =
        SendingTransactionState(
            onBack = {
                // do nothing
            },
            text =
                when (proposal) {
                    is ShieldTransactionProposal -> stringRes(R.string.send_confirmation_sending_subtitle_transparent)
                    is SwapTransactionProposal ->
                        stringRes(R.string.send_confirmation_swapping_subtitle_transparent)

                    else -> stringRes(R.string.send_confirmation_sending_subtitle, getAddressAbbreviated())
                },
            title =
                if (proposal is ShieldTransactionProposal) {
                    stringRes(R.string.send_confirmation_sending_title_transparent)
                } else {
                    stringRes(R.string.send_confirmation_sending_title)
                }
        )

    private suspend fun getAddressAbbreviated(): StringResource {
        val address = (getTransactionProposal() as? SendTransactionProposal)?.destination?.address
        return address?.let { stringResByAddress(it, true) } ?: stringRes("")
    }

    private fun onBackToSendFormAndClear() = viewModelScope.launch { cancelKeystoneProposalFlow(clearSendForm = true) }

    private fun onBackToSendForm() = viewModelScope.launch { cancelKeystoneProposalFlow(clearSendForm = false) }

    private fun onViewTransactions() = viewTransactionsAfterSuccessfulProposal()

    private fun onViewTransactionDetailClick(txId: String) = viewTransactionDetailAfterSuccessfulProposal(txId)
}
