package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import com.keystone.module.DecodeResult
import com.keystone.sdk.KeystoneSDK
import kotlin.jvm.Throws

class DecodeKeystoneSignInRequestUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val keystoneSDK = KeystoneSDK()

    @Throws(InvalidKeystoneSignInQR::class)
    operator fun invoke(result: String): Boolean {
        val decodedResult = decodeResult(result)

        Twig.debug { "=========> progress: " + decodedResult.progress }

        val ur = decodedResult.ur?.toString()

        return if (ur != null) {
            navigationRouter.replace(SelectKeystoneAccount(ur))
            true
        } else {
            false
        }
    }

    @Throws(InvalidKeystoneSignInQR::class)
    private fun decodeResult(result: String): DecodeResult {
        try {
            return keystoneSDK.decodeQR(result)
        } catch (_: Exception) {
            throw InvalidKeystoneSignInQR()
        }
    }
}

class InvalidKeystoneSignInQR : Exception()
