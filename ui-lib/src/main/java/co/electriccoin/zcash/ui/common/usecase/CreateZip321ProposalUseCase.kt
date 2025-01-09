package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransaction

class CreateZip321ProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(zip321Uri: String) {
        try {
            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    keystoneProposalRepository.createZip321Proposal(zip321Uri)
                    keystoneProposalRepository.createPCZTFromProposal()
                }
                is ZashiAccount -> {
                    zashiProposalRepository.createZip321Proposal(zip321Uri)
                }
            }
            navigationRouter.forward(ReviewTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            throw e
        }
    }
}
