package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.SendTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.ShieldTransactionProposal
import co.electriccoin.zcash.ui.common.datasource.Zip321TransactionProposal
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveKeystoneSendTransactionProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
) {
    operator fun invoke(): Flow<SendTransactionProposal?> =
        keystoneProposalRepository.transactionProposal.map {
            when (it) {
                is RegularTransactionProposal -> it
                is Zip321TransactionProposal -> it
                is ShieldTransactionProposal -> null
                null -> null
            }
        }
}
