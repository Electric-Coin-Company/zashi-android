package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.transactionprogress.KeystoneTransactionProgress
import com.keystone.module.DecodeResult
import com.keystone.sdk.KeystoneSDK
import com.sparrowwallet.hummingbird.UR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ParseKeystonePCZTUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) : BaseKeystoneScanner() {

    override suspend fun onSuccess(ur: UR) {
        keystoneProposalRepository.parsePCZT(ur)
        keystoneProposalRepository.extractPCZT()
        navigationRouter.replace(KeystoneTransactionProgress)
    }
}

abstract class BaseKeystoneScanner {
    private val mutex = Mutex()

    private var latestResult: ParseKeystoneQrResult? = null

    protected val keystoneSDK = KeystoneSDK()

    suspend operator fun invoke(result: String): ParseKeystoneQrResult = withSemaphore {
        val latest = latestResult
        if (latest != null && latest.isFinished) {
            latest
        } else {
            val decodedResult = decodeResult(result)
            val ur = decodedResult.ur

            Twig.info { "=========> progress ur: ${decodedResult.progress}" }

            val new = if (ur != null) {
                try {
                    onSuccess(ur)
                    ParseKeystoneQrResult(
                        progress = decodedResult.progress,
                        isFinished = true
                    )
                } catch (e: Exception) {
                    keystoneSDK.resetQRDecoder()
                    ParseKeystoneQrResult(
                        progress = 0,
                        isFinished = false
                    )
                }
            } else {
                ParseKeystoneQrResult(
                    progress = decodedResult.progress,
                    isFinished = false
                )
            }
            latestResult = new
            new
        }
    }

    abstract suspend fun onSuccess(ur: UR)

    private fun decodeResult(result: String): DecodeResult {
        return try {
            keystoneSDK.decodeQR(result)
        } catch (_: Exception) {
            throw InvalidKeystonePCZTQRException()
        }
    }

    private suspend fun <T> withSemaphore(block: suspend () -> T) =
        mutex.withLock {
            withContext(Dispatchers.Default) {
                block()
            }
        }
}

class InvalidKeystonePCZTQRException : Exception()

data class ParseKeystoneQrResult(
    val progress: Int,
    val isFinished: Boolean,
)
