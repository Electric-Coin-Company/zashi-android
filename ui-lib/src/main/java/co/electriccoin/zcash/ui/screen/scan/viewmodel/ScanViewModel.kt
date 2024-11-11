package co.electriccoin.zcash.ui.screen.scan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase.Zip321ParseUriValidation
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs.ADDRESS_BOOK
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs.DEFAULT
import co.electriccoin.zcash.ui.screen.scan.model.ScanResultState
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

internal class ScanViewModel(
    private val args: ScanNavigationArgs,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val zip321ParseUriValidationUseCase: Zip321ParseUriValidationUseCase,
) : ViewModel() {
    val navigateBack = MutableSharedFlow<ScanResultState>()

    val navigateCommand = MutableSharedFlow<String>()

    var state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private var hasBeenScannedSuccessfully = false

    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                if (!hasBeenScannedSuccessfully) {
                    val addressValidationResult = getSynchronizer().validateAddress(result)

                    val zip321ValidationResult = zip321ParseUriValidationUseCase(result)

                    state.update {
                        if (addressValidationResult is AddressType.Valid) {
                            ScanValidationState.INVALID
                        } else if (zip321ValidationResult is Zip321ParseUriValidation.Valid) {
                            ScanValidationState.INVALID
                        } else {
                            ScanValidationState.NONE
                        }
                    }

                    if (zip321ValidationResult is Zip321ParseUriValidation.Valid) {
                        hasBeenScannedSuccessfully = true
                        navigateBack.emit(ScanResultState.Zip321Uri(zip321ValidationResult.zip321Uri))
                    } else if (addressValidationResult is AddressType.Valid) {
                        hasBeenScannedSuccessfully = true

                        val serializableAddress = SerializableAddress(result, addressValidationResult)

                        when (args) {
                            DEFAULT -> {
                                navigateBack.emit(
                                    ScanResultState.Address(
                                        Json.encodeToString(
                                            SerializableAddress.serializer(),
                                            serializableAddress
                                        )
                                    )
                                )
                            }

                            ADDRESS_BOOK -> {
                                navigateCommand.emit(AddContactArgs(serializableAddress.address))
                            }
                        }
                    }
                }
            }
        }

    fun onScannedError() =
        viewModelScope.launch {
            mutex.withLock {
                if (!hasBeenScannedSuccessfully) {
                    state.update { ScanValidationState.INVALID }
                }
            }
        }
}
