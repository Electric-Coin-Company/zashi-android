package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.transactionprogress.KeystoneTransactionProgress
import com.keystone.module.DecodeResult
import com.keystone.sdk.KeystoneSDK
import kotlin.jvm.Throws

class ParseKeystonePCZTUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    private val keystoneSDK = KeystoneSDK()

    @Throws(InvalidKeystonePCZTQR::class)
    suspend operator fun invoke(result: String): Boolean {
        val decodedResult = decodeResult(result)
        Twig.debug { "=========> progress: " + decodedResult.progress }

        val ur = decodedResult.ur

        return if (ur != null) {
            keystoneProposalRepository.parsePCZT(ur)
            keystoneProposalRepository.extractPCZT()
            navigationRouter.replace(KeystoneTransactionProgress)
            true
        } else {
            false
        }
    }

    private fun decodeResult(result: String): DecodeResult {
        return try {
            keystoneSDK.decodeQR(result)
        } catch (_: Exception) {
            throw InvalidKeystonePCZTQR()
        }
    }
}

class InvalidKeystonePCZTQR : Exception()
