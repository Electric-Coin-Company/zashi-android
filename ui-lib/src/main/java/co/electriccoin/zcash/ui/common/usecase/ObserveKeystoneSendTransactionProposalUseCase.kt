package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.RegularTransactionProposal
import co.electriccoin.zcash.ui.common.repository.SendTransactionProposal
import co.electriccoin.zcash.ui.common.repository.ShieldTransactionProposal
import co.electriccoin.zcash.ui.common.repository.Zip321TransactionProposal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveKeystoneSendTransactionProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
) {
    operator fun invoke(): Flow<SendTransactionProposal?> = keystoneProposalRepository.transactionProposal.map {
        when (it) {
            is RegularTransactionProposal -> it
            is Zip321TransactionProposal -> it
            is ShieldTransactionProposal -> null
            null -> null
        }
    }
}
