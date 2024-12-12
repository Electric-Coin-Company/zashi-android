package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.usecase.DecodeKeystoneSignInRequestUseCase
import co.electriccoin.zcash.ui.common.usecase.InvalidKeystoneSignInQR
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneSignInRequestViewModel(
    private val decodeKeystoneSignInRequest: DecodeKeystoneSignInRequestUseCase
) : ViewModel() {
    val state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private var scanSuccess = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (scanSuccess) return@withLock

                try {
                    if (decodeKeystoneSignInRequest(result)) {
                        scanSuccess = true
                    }
                } catch (e: InvalidKeystoneSignInQR) {
                    state.update { ScanValidationState.INVALID }
                }
            }
        }
}
