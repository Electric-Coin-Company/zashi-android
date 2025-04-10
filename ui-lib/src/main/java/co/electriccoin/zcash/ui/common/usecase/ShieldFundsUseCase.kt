package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
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
    private val accountDataSource: AccountDataSource
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    operator fun invoke(navigateBackAfterSuccess: Boolean) {
        scope.launch {
            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    createKeystoneShieldProposal()
                }
                is ZashiAccount -> {
                    if (navigateBackAfterSuccess) {
                        navigationRouter.back()
                    }
                    shieldZashiFunds()
                }
            }
        }
    }

    private suspend fun shieldZashiFunds() {
        try {
            zashiProposalRepository.createShieldProposal()
            zashiProposalRepository.submitTransaction()
            val result = zashiProposalRepository.submitState
                .filterIsInstance<SubmitProposalState.Result>()
                .first()
                .submitResult

            when (result) {
                is SubmitResult.Success -> {
                    // do nothing
                    // TODO messages
                }

                is SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc -> {
                    // showShieldingError(ShieldState.FailedGrpc)
                    // TODO messages
                }

                is SubmitResult.SimpleTrxFailure -> {
                    // showShieldingError(
                    //     ShieldState.Failed(
                    //         error = result.toErrorMessage(),
                    //         stackTrace = result.toErrorStacktrace()
                    //     )
                    // )
                    // TODO messages
                }

                is SubmitResult.MultipleTrxFailure -> {
                    // do nothing
                }
            }
        } finally {
            zashiProposalRepository.clear()
        }
    }

    private suspend fun createKeystoneShieldProposal() {
        try {
            keystoneProposalRepository.createShieldProposal()
            keystoneProposalRepository.createPCZTFromProposal()
            navigationRouter.forward(SignKeystoneTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            // TODO messages
            // showShieldingError(
            //     ShieldState.Failed(
            //         error =
            //             context.getString(
            //                 R.string.balances_shielding_dialog_error_text_below_threshold
            //             ),
            //         stackTrace = ""
            //     )
            // )
        }
    }
}