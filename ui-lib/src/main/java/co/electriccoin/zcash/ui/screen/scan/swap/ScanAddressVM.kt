package co.electriccoin.zcash.ui.screen.scan.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.usecase.NavigateToScanAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase.Zip321ParseUriValidation
import co.electriccoin.zcash.ui.screen.scan.ScanValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanAddressVM(
    private val args: ScanAddressArgs,
    private val parseZip321: Zip321ParseUriValidationUseCase,
    private val navigateToScanAddress: NavigateToScanAddressUseCase,
) : ViewModel() {
    val state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private var hasBeenScannedSuccessfully = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (!hasBeenScannedSuccessfully) {
                    runCatching {
                        when (val zip321ValidationResult = parseZip321(result)) {
                            is Zip321ParseUriValidation.Valid ->
                                onZip321Scanned(zip321ValidationResult)
                            is Zip321ParseUriValidation.SingleAddress ->
                                onZip321SingleAddressScanned(zip321ValidationResult)
                            else -> onAddressScanned(result)
                        }
                    }
                }
            }
        }

    private suspend fun onAddressScanned(result: String) {
        state.update { ScanValidationState.VALID }
        navigateToScanAddress.onScanned(
            address = result,
            amount = null,
            args = args
        )
        hasBeenScannedSuccessfully = true
    }

    private suspend fun onZip321SingleAddressScanned(result: Zip321ParseUriValidation.SingleAddress) {
        state.update { ScanValidationState.VALID }
        navigateToScanAddress.onScanned(
            address = result.address,
            amount = null,
            args = args
        )
        hasBeenScannedSuccessfully = true
    }

    private suspend fun onZip321Scanned(result: Zip321ParseUriValidation.Valid) {
        state.update { ScanValidationState.VALID }
        val address = result.payment.payments[0].recipientAddress.value
        val amount = result.payment.payments[0].nonNegativeAmount.value
        navigateToScanAddress.onScanned(
            address = address,
            amount = amount,
            args = args
        )
        hasBeenScannedSuccessfully = true
    }

    fun onScannedError() =
        viewModelScope.launch {
            mutex.withLock {
                if (!hasBeenScannedSuccessfully) {
                    state.update { ScanValidationState.INVALID }
                }
            }
        }

    fun onBack() = viewModelScope.launch {
        navigateToScanAddress.onScanCancelled(args)
    }
}
