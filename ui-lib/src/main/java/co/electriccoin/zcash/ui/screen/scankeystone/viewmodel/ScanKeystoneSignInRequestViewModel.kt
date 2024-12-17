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

    fun onScanned(result: String) =
        viewModelScope.launch {
            try {
                val scanResult = parseKeystoneSignInRequest(result)
                state.update { it.copy(progress = scanResult.progress) }
            } catch (_: InvalidKeystoneSignInQRException) {
                validationState.update { ScanValidationState.INVALID }
            } catch (_: Exception) {
                // do nothing
            }
        }
}
