package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.transactionprogress.KeystoneTransactionProgress
import com.keystone.sdk.KeystoneSDK

class ParseKeystonePCZTUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    private val keystoneSDK = KeystoneSDK()

    suspend operator fun invoke(result: String): Boolean {
        return try {
            val decodedResult = keystoneSDK.decodeQR(result)
            Twig.debug { "=========> progress: " + decodedResult.progress }

            val ur = decodedResult.ur

            if (ur != null && keystoneProposalRepository.parsePCZT(ur)) {
                keystoneProposalRepository.extractPCZT()
                navigationRouter.replace(KeystoneTransactionProgress)
            }

            true
        } catch (_: Exception) {
            false
        }
    }
}
