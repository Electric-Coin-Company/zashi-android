package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository

class CancelKeystoneProposalFlowUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter,
    private val observeClearSend: ObserveClearSendUseCase
) {

    operator fun invoke() {
        keystoneProposalRepository.clear()
        observeClearSend.requestClear()
        navigationRouter.backToRoot()
    }
}