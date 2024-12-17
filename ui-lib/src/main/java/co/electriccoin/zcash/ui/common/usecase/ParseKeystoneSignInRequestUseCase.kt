package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import com.sparrowwallet.hummingbird.UR

class ParseKeystoneSignInRequestUseCase(
    private val navigationRouter: NavigationRouter
) : BaseKeystoneScanner() {

    override suspend fun onSuccess(ur: UR) {
        tryParse(ur)
        navigationRouter.replace(SelectKeystoneAccount(ur.toString()))
    }

    @Suppress("TooGenericExceptionCaught")
    @Throws(InvalidKeystoneSignInQRException::class)
    private fun tryParse(ur: UR) {
        try {
            keystoneSDK.parseZcashAccounts(ur)
        } catch (e: Exception) {
            throw InvalidKeystoneSignInQRException(e)
        }
    }
}

class InvalidKeystoneSignInQRException(cause: Throwable) : Exception(cause)
