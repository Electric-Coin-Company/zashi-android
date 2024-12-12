package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewKeystoneTransaction

class CreateKeystoneProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(zecSend: ZecSend): Boolean {
        return if (
            keystoneProposalRepository.createProposal(zecSend) &&
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
