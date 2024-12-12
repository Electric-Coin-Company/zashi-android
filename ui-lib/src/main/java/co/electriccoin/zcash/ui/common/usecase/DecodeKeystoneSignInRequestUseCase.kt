package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import com.keystone.sdk.KeystoneSDK

class DecodeKeystoneSignInRequestUseCase(
    private val navigationRouter: NavigationRouter
) {
    private val keystoneSDK = KeystoneSDK()

    operator fun invoke(result: String): Boolean {
        return try {
            val decodedResult = keystoneSDK.decodeQR(result)

            Twig.debug { "=========> progress: " + decodedResult.progress }

            val ur = decodedResult.ur?.toString()

            if (ur != null) {
                navigationRouter.replace(SelectKeystoneAccount(ur))
                true
            } else {
                false
            }
        } catch (_: Exception) {
            throw InvalidKeystoneSignInQR()
        }
    }
}

class InvalidKeystoneSignInQR : Exception()
