package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneSignInRequestUseCase
import co.electriccoin.zcash.ui.common.usecase.InvalidKeystoneSignInQRException
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneSignInRequestViewModel(
    private val parseKeystoneSignInRequest: ParseKeystoneSignInRequestUseCase
) : ViewModel() {
    val state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private var scanSuccess = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (scanSuccess) return@withLock

                try {
                    if (parseKeystoneSignInRequest(result)) {
                        scanSuccess = true
                    }
                } catch (_: InvalidKeystoneSignInQRException) {
                    state.update { ScanValidationState.INVALID }
                } catch (_: Exception) {
                    // do nothing
                }
            }
        }
}
