package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.SubmitProposalState
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShieldFundsUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val navigationRouter: NavigationRouter,
    private val accountDataSource: AccountDataSource,
    private val navigateToError: NavigateToErrorUseCase,
    private val messageAvailabilityDataSource: MessageAvailabilityDataSource,
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    operator fun invoke(closeCurrentScreen: Boolean) {
        scope.launch {
            messageAvailabilityDataSource.onShieldingInitiated()

            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    createKeystoneShieldProposal()
                }

                is ZashiAccount -> {
                    if (closeCurrentScreen) {
                        navigationRouter.back()
                    }
                    shieldZashiFunds()
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun shieldZashiFunds() {
        try {
            zashiProposalRepository.createShieldProposal()
            zashiProposalRepository.submitTransaction()
            val result =
                zashiProposalRepository.submitState
                    .filterIsInstance<SubmitProposalState.Result>()
                    .first()
                    .submitResult

            if (result is SubmitResult.Failure) {
                navigateToError(ErrorArgs.ShieldingError(result))
            }
        } catch (e: Exception) {
            navigateToError(ErrorArgs.ShieldingGeneralError(e))
        } finally {
            zashiProposalRepository.clear()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun createKeystoneShieldProposal() {
        try {
            keystoneProposalRepository.createShieldProposal()
            keystoneProposalRepository.createPCZTFromProposal()
            navigationRouter.forward(SignKeystoneTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            navigateToError(ErrorArgs.ShieldingGeneralError(e))
        }
    }
}
