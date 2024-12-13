package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewKeystoneTransaction

class CreateKeystoneProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(zecSend: ZecSend) {
        try {
            keystoneProposalRepository.createProposal(zecSend)
            keystoneProposalRepository.createPCZTFromProposal()
            navigationRouter.forward(ReviewKeystoneTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            throw e
        }
    }
}
