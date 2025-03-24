package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.send.Send

class CancelProposalFlowUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val navigationRouter: NavigationRouter,
    private val observeClearSend: ObserveClearSendUseCase,
) {
    operator fun invoke(clearSendForm: Boolean = true) {
        zashiProposalRepository.clear()
        keystoneProposalRepository.clear()
        if (clearSendForm) {
            observeClearSend.requestClear()
        }
        navigationRouter.backTo(Send::class)
    }
}
