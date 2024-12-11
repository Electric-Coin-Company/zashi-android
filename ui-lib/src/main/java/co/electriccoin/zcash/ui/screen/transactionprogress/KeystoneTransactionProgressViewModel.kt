package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.SendTransactionProposal
import co.electriccoin.zcash.ui.common.repository.ShieldTransactionProposal
import co.electriccoin.zcash.ui.common.repository.SubmitProposalState
import co.electriccoin.zcash.ui.common.usecase.CancelKeystoneProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.SendEmailUseCase
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
    private val navigationRouter: NavigationRouter,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val cancelKeystoneProposalFlow: CancelKeystoneProposalFlowUseCase
) : ViewModel() {
    private val supportContacted = MutableStateFlow(false)

    val state: StateFlow<TransactionProgressState?> =
        combine(keystoneProposalRepository.submitState, supportContacted) { submitState, supportContacted ->
            when (submitState) {
                null, SubmitProposalState.Submitting -> createSendingTransactionState()
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

                        SubmitResult.Success -> createSuccessfulTransactionState()
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
                onBackToHomepageAndClearDataRequested()
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
            onBack = ::onBackToHomepageAndClearDataRequested,
            onCloseClick = ::onBackToHomepageAndClearDataRequested
        )

    private suspend fun createSuccessfulTransactionState() =
        SuccessfulTransactionState(
            onBack = ::onBackToHomepageAndClearDataRequested,
            onViewTransactionClick = ::onBackToHomepageAndClearDataRequested,
            onCloseClick = ::onBackToHomepageAndClearDataRequested,
            address = getAddressAbbreviated()
        )

    private fun createFailureTransactionState(result: SubmitResult.SimpleTrxFailure) =
        FailureTransactionState(
            onBack = {
                navigationRouter.back()
            },
            onCloseClick = {
                navigationRouter.back()
            },
            onViewTransactionClick = ::onBackToHomepageAndClearDataRequested,
            onReportClick = {
                viewModelScope.launch {
                    sendEmailUseCase(result)
                    this@KeystoneTransactionProgressViewModel.supportContacted.update { true }
                }
            }
        )

    private suspend fun createSendingTransactionState() =
        SendingTransactionState(
            onBack = {
                // do nothing
            },
            address = getAddressAbbreviated()
        )

    private suspend fun getAddressAbbreviated(): String {
        val address = when (val proposal = keystoneProposalRepository.getTransactionProposal()) {
            is ShieldTransactionProposal -> getSelectedWalletAccount().unified.address.address
            is SendTransactionProposal -> proposal.destination.address
        }

        return "${address.take(ADDRESS_MAX_LENGTH)}..."
    }

    private fun onBackToHomepageAndClearDataRequested() {
        cancelKeystoneProposalFlow()
    }
}
