package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransaction

class CreateKeystoneShieldProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke() {
        try {
            keystoneProposalRepository.createShieldProposal()
            keystoneProposalRepository.createPCZTFromProposal()
            navigationRouter.forward(SignKeystoneTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            throw e
        }
    }
}
