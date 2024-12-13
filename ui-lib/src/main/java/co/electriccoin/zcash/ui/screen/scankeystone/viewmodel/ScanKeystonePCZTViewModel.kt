package co.electriccoin.zcash.ui.screen.scankeystone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.common.repository.ParsePCZTException
import co.electriccoin.zcash.ui.common.usecase.InvalidKeystonePCZTQRException
import co.electriccoin.zcash.ui.common.usecase.ParseKeystonePCZTUseCase
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystonePCZTViewModel(
    private val parseKeystonePCZT: ParseKeystonePCZTUseCase
) : ViewModel() {
    var state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private var scanSuccess = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (scanSuccess) return@withLock

                try {
                    if (parseKeystonePCZT(result)) {
                        scanSuccess = true
                    }
                } catch (_: InvalidKeystonePCZTQRException) {
                    state.update { ScanValidationState.INVALID }
                } catch (_: ParsePCZTException) {
                    state.update { ScanValidationState.INVALID }
                } catch (_: Exception) {
                    // do nothing
                }
            }
        }
}
