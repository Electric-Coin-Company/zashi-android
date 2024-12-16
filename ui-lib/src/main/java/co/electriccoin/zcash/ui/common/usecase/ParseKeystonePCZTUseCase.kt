package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.screen.transactionprogress.KeystoneTransactionProgress
import com.keystone.module.DecodeResult
import com.sparrowwallet.hummingbird.ResultType
import com.sparrowwallet.hummingbird.URDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

class ParseKeystonePCZTUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val navigationRouter: NavigationRouter
) {
    private val keystoneDecoder = KeystoneDecoder()

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
            keystoneDecoder.decodeQR(result)
        } catch (_: Exception) {
            throw InvalidKeystonePCZTQRException()
        }
    }
}

class InvalidKeystonePCZTQRException : Exception()

private class KeystoneDecoder {
    private var urDecoder: URDecoder = URDecoder()

    @Suppress("MagicNumber", "TooGenericExceptionThrown")
    fun decodeQR(qr: String): DecodeResult {
        val isReceived = urDecoder.receivePart(qr)
        if (urDecoder.result == null) {
            if (isReceived) {
                val processedParts = urDecoder.processedPartsCount.toFloat()
                val expectedParts = urDecoder.expectedPartCount.toFloat() * 1.75f
                val progress = (processedParts / expectedParts) * 100
                return DecodeResult(min(100, progress.toInt()))
            } else {
                resetQRDecoder()
                throw Exception("Unexpected QR code")
            }
        }
        when (urDecoder.result.type) {
            ResultType.SUCCESS -> {
                return DecodeResult(100, urDecoder.result.ur)
            }
            else -> {
                resetQRDecoder()
                throw Exception("Invalid QR code")
            }
        }
    }

    private fun resetQRDecoder() {
        urDecoder = URDecoder()
    }
}
