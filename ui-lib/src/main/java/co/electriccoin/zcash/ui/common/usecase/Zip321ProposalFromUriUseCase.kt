package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.Proposal
import co.electriccoin.zcash.spackle.Twig

class ProposalFromZip321UriUseCase(
    private val getSynchronizerUseCase: GetSynchronizerUseCase
) {
    suspend fun invoke(zip321Uri: String) = getProposal(zip321Uri)

    private suspend fun getProposal(zip321Uri: String): Proposal {
        val proposal = getSynchronizerUseCase.invoke().proposeFulfillingPaymentUri(Account.DEFAULT, zip321Uri)

        Twig.info { "Request Zip321 proposal: $proposal" }

        return proposal
    }
}
