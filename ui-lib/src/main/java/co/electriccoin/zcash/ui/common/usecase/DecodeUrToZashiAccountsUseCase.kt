package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.spackle.Twig
import com.google.gson.Gson
import com.keystone.module.ZcashAccounts
import com.keystone.sdk.KeystoneSDK
import com.sparrowwallet.hummingbird.UR
import com.sparrowwallet.hummingbird.URDecoder

class DecodeUrToZashiAccountsUseCase {
    private val sdk = KeystoneSDK()

    operator fun invoke(urRaw: String): ZcashAccounts? {
        val ur = URDecoder.decode(urRaw) ?: return null
        return getAccountsFromKeystone(ur)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun getAccountsFromKeystone(ur: UR): ZcashAccounts? {
        return try {
            val accounts = sdk.parseZcashAccounts(ur)
            Twig.debug { "=========> progress: " + Gson().toJson(accounts).toString() }
            accounts
        } catch (_: Exception) {
            null
        }
    }
}
