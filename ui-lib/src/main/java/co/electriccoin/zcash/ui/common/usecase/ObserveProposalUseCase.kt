package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class ObserveProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val accountDataSource: AccountDataSource,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() =
        accountDataSource.selectedAccount
            .filterNotNull()
            .flatMapLatest {
                when (it) {
                    is KeystoneAccount -> keystoneProposalRepository.transactionProposal
                    is ZashiAccount -> zashiProposalRepository.transactionProposal
                }
            }
            .filterNotNull()

    fun filterSendTransactions() =
        this()
            .filterIsInstance<SendTransactionProposal>()
}
