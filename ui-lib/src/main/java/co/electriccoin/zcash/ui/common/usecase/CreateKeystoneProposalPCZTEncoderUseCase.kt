package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import com.sparrowwallet.hummingbird.UREncoder

class CreateKeystoneProposalPCZTEncoderUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
) {
    suspend operator fun invoke(): UREncoder {
        return keystoneProposalRepository.createPCZTEncoder()
    }
}