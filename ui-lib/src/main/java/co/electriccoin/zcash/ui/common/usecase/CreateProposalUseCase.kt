package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransaction

class CreateProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(zecSend: ZecSend) {
        try {
            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    keystoneProposalRepository.createProposal(zecSend)
                    keystoneProposalRepository.createPCZTFromProposal()
                }
                is ZashiAccount ->
                    zashiProposalRepository.createProposal(zecSend)
            }
            navigationRouter.forward(ReviewTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            zashiProposalRepository.clear()
            throw e
        }
    }
}
