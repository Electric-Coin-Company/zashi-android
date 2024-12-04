package co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationExpandedInfoState
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationState
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CreateTransactionsViewModel(
    observeSelectedWalletAccountUseCase: ObserveSelectedWalletAccountUseCase
) : ViewModel() {
    // Technically this value will not survive process dead, but will survive all possible configuration changes
    // Possible solution would be storing the value within [SavedStateHandle]
    val submissions: MutableStateFlow<List<TransactionSubmitResult>> = MutableStateFlow(emptyList())

    val state = observeSelectedWalletAccountUseCase().map {
        SendConfirmationState(
            SendConfirmationExpandedInfoState(
                title = stringRes("Sending from"), // TODO keystone string
                icon = it.icon,
                text = it.name
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = SendConfirmationState(from = null)
    )

    suspend fun runCreateTransactions(
        synchronizer: Synchronizer,
        spendingKey: UnifiedSpendingKey,
        proposal: Proposal
    ): SubmitResult {
        val submitResults = mutableListOf<TransactionSubmitResult>()

        return runCatching {
            synchronizer.createProposedTransactions(
                proposal = proposal,
                usk = spendingKey
            ).collect { submitResult ->
                Twig.info { "Transaction submit result: $submitResult" }
                submitResults.add(submitResult)
            }
            if (submitResults.find { it is TransactionSubmitResult.Failure } != null) {
                if (submitResults.size == 1) {
                    // The first transaction submission failed - user might just be able to re-submit the transaction
                    // proposal. Simple error pop up is fine then
                    val result = (submitResults[0] as TransactionSubmitResult.Failure)
                    if (result.grpcError) {
                        SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc(result)
                    } else {
                        SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit(result)
                    }
                } else {
                    // Any subsequent transaction submission failed - user needs to resolve this manually. Multiple
                    // transaction failure screen presented
                    SubmitResult.MultipleTrxFailure(submitResults)
                }
            } else {
                // All transaction submissions were successful
                SubmitResult.Success
            }
        }.onSuccess {
            Twig.debug { "Transactions submitted successfully" }
        }.onFailure {
            Twig.error(it) { "Transactions submission failed" }
        }.getOrElse {
            SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(it)
        }.also {
            // Save the submission results for the later MultipleSubmissionError screen
            if (it is SubmitResult.MultipleTrxFailure) {
                submissions.value = submitResults
            }
        }
    }
}
