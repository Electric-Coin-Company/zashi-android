package co.electriccoin.zcash.ui.screen.scan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs
import co.electriccoin.zcash.ui.screen.scan.ScanNavigationArgs.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class ScanViewModel(
    private val args: ScanNavigationArgs,
    private val getSynchronizer: GetSynchronizerUseCase
) : ViewModel() {

    val navigateBack = MutableSharedFlow<String>()

    val navigateToAddressBook = MutableSharedFlow<String>()

    var state = MutableStateFlow<AddressType?>(null)

    private val mutex = Mutex()

    private var hasBeenScannedSuccessfully = false

    fun onScanned(result: String) = viewModelScope.launch {
        viewModelScope.launch {
            mutex.withLock {
                if (!hasBeenScannedSuccessfully) {
                    val addressValidationResult = getSynchronizer().validateAddress(result)
                    state.update { addressValidationResult }
                    if (addressValidationResult.isNotValid.not()) {
                        hasBeenScannedSuccessfully = true

                        val serializableAddress = SerializableAddress(result, addressValidationResult)

                        when (args) {
                            DEFAULT -> {
                                navigateBack.emit(
                                    Json.encodeToString(
                                        SerializableAddress.serializer(),
                                        serializableAddress
                                    )
                                )
                            }

                            ADDRESS_BOOK -> {
                                navigateToAddressBook.emit(AddContactArgs(serializableAddress.address))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onScannedError() = viewModelScope.launch {
        mutex.withLock {
            if (!hasBeenScannedSuccessfully) {
                state.update { AddressType.Invalid() }
            }
        }
    }
}