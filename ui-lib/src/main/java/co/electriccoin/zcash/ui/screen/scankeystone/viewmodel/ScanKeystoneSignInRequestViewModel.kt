package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import com.keystone.sdk.KeystoneSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneSignInRequestViewModel(
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    var state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private val sdk = KeystoneSDK()

    private var scanSuccess = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (scanSuccess) return@withLock

                val decodedResult = sdk.decodeQR(result)
                Twig.debug { "=========> progress: " + decodedResult.progress }

                val ur = decodedResult.ur?.toString()

                if (ur != null) {
                    scanSuccess = true
                    navigationRouter.replace(SelectKeystoneAccount(ur))
                }
            }
        }
}
