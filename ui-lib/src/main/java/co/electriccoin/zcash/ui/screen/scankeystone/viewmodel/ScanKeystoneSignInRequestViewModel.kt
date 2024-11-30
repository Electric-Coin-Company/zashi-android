package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import com.google.gson.Gson
import com.keystone.module.DecodeResult
import com.keystone.module.ZcashAccounts
import com.keystone.sdk.KeystoneSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneSignInRequestViewModel : ViewModel() {
    var state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private val sdk = KeystoneSDK()

    private val gson = Gson()

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                val decodedResult = sdk.decodeQR(result)
                Twig.debug { "=========> progress: " + decodedResult.progress }

                val accounts = getAccountsFromKeystone(decodedResult) ?: getAccountsFromURResult(decodedResult)

                Twig.debug { "=========> progress: $accounts"}
            }
        }

    @Suppress("TooGenericExceptionCaught")
    private fun getAccountsFromKeystone(decodedResult: DecodeResult): ZcashAccounts? {
        val ur = decodedResult.ur ?: return null
        return try {
            val accounts = sdk.parseZcashAccounts(ur)
            Twig.debug { "=========> progress: " + Gson().toJson(accounts).toString() }
            accounts
        } catch (_: Exception) {
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getAccountsFromURResult(decodedResult: DecodeResult): ZcashAccounts? {
        val ur = decodedResult.ur ?: return null
        val json = gson.toJson(UR(ur.type, ur.cborBytes.toHexString()))
        Twig.debug { "=========> progress: $json" }
        return gson.fromJson(json, ZcashAccounts::class.java)
    }
}

private data class UR(val type: String, val cbor: String)
