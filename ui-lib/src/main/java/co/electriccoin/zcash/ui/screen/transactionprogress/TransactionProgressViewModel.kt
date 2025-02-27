package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ShieldTransactionProposal
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
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionProgressViewModel(
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
                        is SubmitResult.MultipleTrxFailure ->
                            createMultipleFailuresTransactionState(supportContacted, result)

                        is SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc ->
                            createGrpcFailureTransactionState()

                        is SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther ->
                            createFailureTransactionState(proposal, result)

                        is SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit ->
                            createFailureTransactionState(proposal, result)

                        is SubmitResult.Success -> createSuccessfulTransactionState(proposal, result)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createMultipleFailuresTransactionState(
        supportContacted: Boolean,
        result: SubmitResult.MultipleTrxFailure
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
                tag = "Transaction ID",
                value = result.results.joinToString(separator = ", ") { it.txIdString() }
            )
        },
        onSupportClick = {
            viewModelScope.launch {
                sendEmailUseCase(result)
                this@TransactionProgressViewModel.supportContacted.update { true }
            }
        },
        transactionIds = result.results.map { it.txIdString() }
    )

    private fun createGrpcFailureTransactionState() =
        GrpcFailureTransactionState(
            onBack = ::onViewTransactions,
            onCloseClick = ::onViewTransactions
        )

    private suspend fun createSuccessfulTransactionState(
        proposal: TransactionProposal?,
        result: SubmitResult.Success
    ) = SuccessfulTransactionState(
        onBack = ::onViewTransactions,
        onViewTransactionClick = {
            val txId = result.txIds.firstOrNull()
            if (txId == null) {
                onViewTransactions()
            } else {
                onViewTransactionDetailClick(txId)
            }
        },
        onCloseClick = ::onViewTransactions,
        text =
            if (proposal is ShieldTransactionProposal) {
                stringRes(R.string.send_confirmation_success_subtitle_transparent)
            } else {
                stringRes(R.string.send_confirmation_success_subtitle, getAddressAbbreviated())
            },
        title =
            if (proposal is ShieldTransactionProposal) {
                stringRes(R.string.send_confirmation_success_title_transparent)
            } else {
                stringRes(R.string.send_confirmation_success_title, getAddressAbbreviated())
            }
    )

    private fun createFailureTransactionState(
        proposal: TransactionProposal?,
        result: SubmitResult.SimpleTrxFailure,
    ) = FailureTransactionState(
        onBack = ::onBackToSendForm,
        onCloseClick = ::onBackToSendForm,
        onViewTransactionClick = {
            if (result is SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit) {
                onViewTransactionDetailClick(result.result.txIdString())
            } else {
                onViewTransactions()
            }
        },
        onReportClick = {
            viewModelScope.launch {
                sendEmailUseCase(result)
                this@TransactionProgressViewModel.supportContacted.update { true }
            }
        },
        title =
            if (proposal is ShieldTransactionProposal) {
                stringRes(R.string.send_confirmation_failure_title_transparent)
            } else {
                stringRes(R.string.send_confirmation_failure_title)
            },
        text =
            if (proposal is ShieldTransactionProposal) {
                stringRes(R.string.send_confirmation_failure_subtitle_transparent)
            } else {
                stringRes(R.string.send_confirmation_failure_subtitle)
            }
    )

    private suspend fun createSendingTransactionState(proposal: TransactionProposal?) =
        SendingTransactionState(
            onBack = {
                // do nothing
            },
            text =
                if (proposal is ShieldTransactionProposal) {
                    stringRes(R.string.send_confirmation_sending_subtitle_transparent)
                } else {
                    stringRes(R.string.send_confirmation_sending_subtitle, getAddressAbbreviated())
                },
            title =
                if (proposal is ShieldTransactionProposal) {
                    stringRes(R.string.send_confirmation_sending_title_transparent)
                } else {
                    stringRes(R.string.send_confirmation_sending_title)
                }
        )

    private suspend fun getAddressAbbreviated(): String {
        val address = (getTransactionProposal() as? SendTransactionProposal)?.destination?.address
        return address?.let { "${it.take(ADDRESS_MAX_LENGTH)}..." }.orEmpty()
    }

    private fun onBackToSendFormAndClear() {
        cancelKeystoneProposalFlow(clearSendForm = true)
    }

    private fun onBackToSendForm() {
        cancelKeystoneProposalFlow(clearSendForm = false)
    }

    private fun onViewTransactions() {
        viewTransactionsAfterSuccessfulProposal()
    }

    private fun onViewTransactionDetailClick(txId: String) {
        viewTransactionDetailAfterSuccessfulProposal(txId)
    }
}
