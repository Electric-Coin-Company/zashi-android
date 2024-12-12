package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransaction

class CreateKeystoneShieldProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke() {
        keystoneProposalRepository.createShieldProposal()
        keystoneProposalRepository.createPCZTFromProposal()
        // keystoneProposalRepository.addPCZTToProofs()
        navigationRouter.forward(SignKeystoneTransaction)
    }
}
