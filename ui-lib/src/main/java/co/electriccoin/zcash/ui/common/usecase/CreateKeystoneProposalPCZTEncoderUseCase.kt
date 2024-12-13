package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.PcztNotCreatedException
import com.sparrowwallet.hummingbird.UREncoder
import kotlin.jvm.Throws

class CreateKeystoneProposalPCZTEncoderUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
) {
    @Throws(PcztNotCreatedException::class)
    suspend operator fun invoke(): UREncoder {
        return keystoneProposalRepository.createPCZTEncoder()
    }
}
