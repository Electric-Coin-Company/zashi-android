package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewKeystoneTransaction

class CreateKeystoneZip321ProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(zip321Uri: String): Boolean {
        return if (
            keystoneProposalRepository.createZip321Proposal(zip321Uri) &&
            keystoneProposalRepository.createPCZTFromProposal() &&
            keystoneProposalRepository.addPCZTToProofs()
        ) {
            navigationRouter.forward(ReviewKeystoneTransaction)
            true
        } else {
            false
        }
    }
}
