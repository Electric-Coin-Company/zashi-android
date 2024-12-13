package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.InvalidKeystoneSignInQRException
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneSignInRequestUseCase
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import co.electriccoin.zcash.ui.screen.scankeystone.model.ScanKeystoneState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneSignInRequestViewModel(
    private val parseKeystoneSignInRequest: ParseKeystoneSignInRequestUseCase
) : ViewModel() {
    val validationState = MutableStateFlow(ScanValidationState.NONE)

    val state =
        MutableStateFlow(
            ScanKeystoneState(
                progress = null,
                message = stringRes(R.string.scan_keystone_info),
            )
        )

    private val mutex = Mutex()

    private var scanSuccess = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (scanSuccess) return@withLock

                try {
                    val scanResult = parseKeystoneSignInRequest(result)
                    state.update { it.copy(progress = scanResult.progress) }
                    if (scanResult.isFinished) {
                        scanSuccess = true
                    }
                } catch (_: InvalidKeystoneSignInQRException) {
                    validationState.update { ScanValidationState.INVALID }
                } catch (_: Exception) {
                    // do nothing
                }
            }
        }
}
