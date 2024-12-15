package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.transactionprogress.KeystoneTransactionProgress
import com.keystone.module.DecodeResult
import com.keystone.sdk.KeystoneSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParseKeystonePCZTUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    private val keystoneSDK = KeystoneSDK()

    suspend operator fun invoke(result: String): ParseKeystoneQrResult =
        withContext(Dispatchers.Default) {
            val decodedResult = decodeResult(result)
            val ur = decodedResult.ur

            Twig.info { "=========> progress ur: ${decodedResult.progress}" }

            if (ur != null) {
                keystoneProposalRepository.parsePCZT(ur)
                keystoneProposalRepository.extractPCZT()
                navigationRouter.replace(KeystoneTransactionProgress)
                ParseKeystoneQrResult(
                    progress = decodedResult.progress,
                    isFinished = true
                )
            } else {
                ParseKeystoneQrResult(
                    progress = decodedResult.progress,
                    isFinished = false
                )
            }
        }

    private fun decodeResult(result: String): DecodeResult {
        return try {
            keystoneSDK.decodeQR(result)
        } catch (_: Exception) {
            throw InvalidKeystonePCZTQRException()
        }
    }
}

class InvalidKeystonePCZTQRException : Exception()
