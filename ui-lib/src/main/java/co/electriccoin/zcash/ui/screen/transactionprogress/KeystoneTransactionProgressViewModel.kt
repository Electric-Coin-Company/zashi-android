package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ShieldTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.TransactionProposal
import co.electriccoin.zcash.ui.common.datasource.Zip321TransactionProposal
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.SubmitProposalState
import co.electriccoin.zcash.ui.common.usecase.CancelKeystoneProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.SendEmailUseCase
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeystoneTransactionProgressViewModel(
    private val getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val cancelKeystoneProposalFlow: CancelKeystoneProposalFlowUseCase
) : ViewModel() {
    private val supportContacted = MutableStateFlow(false)

    val state: StateFlow<TransactionProgressState?> =
        combine(
            keystoneProposalRepository.transactionProposal,
            keystoneProposalRepository.submitState,
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
                            createFailureTransactionState(result)

                        is SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit ->
                            createFailureTransactionState(result)

                        SubmitResult.Success -> createSuccessfulTransactionState(proposal)
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
                onBackToHomepageAndClearSendForm()
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
                this@KeystoneTransactionProgressViewModel.supportContacted.update { true }
            }
        },
        transactionIds = result.results.map { it.txIdString() }
    )

    private fun createGrpcFailureTransactionState() =
        GrpcFailureTransactionState(
            onBack = ::onBackToHomepageAndClearSendForm,
            onCloseClick = ::onBackToHomepageAndClearSendForm
        )

    private suspend fun createSuccessfulTransactionState(proposal: TransactionProposal?) =
        SuccessfulTransactionState(
            onBack = ::onBackToHomepageAndClearSendForm,
            onViewTransactionClick = ::onBackToHomepageAndClearSendForm,
            onCloseClick = ::onBackToHomepageAndClearSendForm,
            text = when (proposal) {
                is RegularTransactionProposal ->
                    stringRes(R.string.send_confirmation_success_subtitle, getAddressAbbreviated())
                is Zip321TransactionProposal ->
                    stringRes(R.string.send_confirmation_success_subtitle, getAddressAbbreviated())
                is ShieldTransactionProposal ->
                    stringRes(R.string.send_confirmation_success_subtitle_transparent)
                null ->
                    stringRes(R.string.send_confirmation_success_subtitle, getAddressAbbreviated())
            }
        )

    private fun createFailureTransactionState(result: SubmitResult.SimpleTrxFailure) =
        FailureTransactionState(
            onBack = ::onBackToHomepage,
            onCloseClick = ::onBackToHomepage,
            onViewTransactionClick = ::onBackToHomepageAndClearSendForm,
            onReportClick = {
                viewModelScope.launch {
                    sendEmailUseCase(result)
                    this@KeystoneTransactionProgressViewModel.supportContacted.update { true }
                }
            }
        )

    private suspend fun createSendingTransactionState(proposal: TransactionProposal?) =
        SendingTransactionState(
            onBack = {
                // do nothing
            },
            text = when (proposal) {
                is RegularTransactionProposal ->
                    stringRes(R.string.send_confirmation_sending_subtitle, getAddressAbbreviated())
                is Zip321TransactionProposal ->
                    stringRes(R.string.send_confirmation_sending_subtitle, getAddressAbbreviated())
                is ShieldTransactionProposal ->
                    stringRes(R.string.send_confirmation_sending_subtitle_transparent)
                null ->
                    stringRes(R.string.send_confirmation_sending_subtitle, getAddressAbbreviated())
            }
        )

    private suspend fun getAddressAbbreviated(): String {
        val address = (keystoneProposalRepository.getTransactionProposal() as? SendTransactionProposal)
            ?.destination?.address
        return address?.let { "${it.take(ADDRESS_MAX_LENGTH)}..." }.orEmpty()
    }

    private fun onBackToHomepageAndClearSendForm() {
        cancelKeystoneProposalFlow(clearSendForm = true)
    }

    private fun onBackToHomepage() {
        cancelKeystoneProposalFlow(clearSendForm = false)
    }
}
