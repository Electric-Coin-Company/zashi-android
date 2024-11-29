package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import com.google.gson.Gson
import com.keystone.sdk.KeystoneSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneViewModel : ViewModel() {
    var state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private val sdk = KeystoneSDK()

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    @OptIn(ExperimentalStdlibApi::class)
    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                val decodedResult = sdk.decodeQR(result)
                Twig.debug { "=========> progress: " + decodedResult.progress }

                try {
                    val accounts = sdk.parseMultiAccounts(decodedResult.ur!!)
                    Twig.debug { "=========> progress: " + Gson().toJson(accounts).toString() }
                } catch (err: Exception) {
                    if (decodedResult.ur != null) {
                        val ur = decodedResult.ur!!
                        val gson = Gson().toJson(UR(ur.type, ur.cborBytes.toHexString()))
                        Twig.debug { "=========> progress: $gson" }
                    }
                }
            }
        }
}

private data class UR(val type: String, val cbor: String)
