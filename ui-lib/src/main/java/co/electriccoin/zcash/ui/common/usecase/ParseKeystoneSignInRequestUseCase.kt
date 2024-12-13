package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import com.keystone.module.DecodeResult
import com.keystone.sdk.KeystoneSDK
import com.sparrowwallet.hummingbird.UR
import kotlin.jvm.Throws

class ParseKeystoneSignInRequestUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val keystoneSDK = KeystoneSDK()

    @Throws(InvalidKeystoneSignInQRException::class)
    operator fun invoke(result: String): Boolean {
        val decodedResult = decodeResult(result)

        Twig.debug { "=========> progress: " + decodedResult.progress }

        val ur = decodedResult.ur

        return if (ur != null) {
            tryParse(ur)
            navigationRouter.replace(SelectKeystoneAccount(ur.toString()))
            true
        } else {
            false
        }
    }

    @Throws(InvalidKeystoneSignInQRException::class)
    private fun tryParse(ur: UR) {
        try {
            keystoneSDK.parseZcashAccounts(ur)
        } catch (_: Exception) {
            throw InvalidKeystoneSignInQRException()
        }
    }

    @Throws(InvalidKeystoneSignInQRException::class)
    private fun decodeResult(result: String): DecodeResult {
        try {
            return keystoneSDK.decodeQR(result)
        } catch (_: Exception) {
            throw InvalidKeystoneSignInQRException()
        }
    }
}

class InvalidKeystoneSignInQRException : Exception()
