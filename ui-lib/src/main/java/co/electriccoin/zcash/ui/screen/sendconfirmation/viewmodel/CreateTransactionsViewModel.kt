package co.electriccoin.zcash.ui.screen.sendconfirmation.viewmodel

import androidx.lifecycle.ViewModel
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import kotlinx.coroutines.flow.MutableStateFlow

class CreateTransactionsViewModel : ViewModel() {
    // Technically this value will not survive process dead, but will survive all possible configuration changes
    // Possible solution would be storing the value within [SavedStateHandle]
    val submissions: MutableStateFlow<List<TransactionSubmitResult>> = MutableStateFlow(emptyList())

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
                    SubmitResult.SimpleTrxFailure(
                        errorDescription =
                            buildString {
                                val result = (submitResults[0] as TransactionSubmitResult.Failure)
                                appendLine("Error code: ${result.code}")
                                appendLine("Is gRPC error: ${result.grpcError}")
                                appendLine(result.description ?: "")
                            }
                    )
                } else {
                    // Any subsequent transaction submission failed - user needs to resolve this manually. Multiple
                    // transaction failure screen presented
                    SubmitResult.MultipleTrxFailure
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
            SubmitResult.SimpleTrxFailure(it.message ?: "")
        }.also {
            // Save the submission results for the later MultipleSubmissionError screen
            if (it == SubmitResult.MultipleTrxFailure) {
                submissions.value = submitResults
            }
        }
    }
}
