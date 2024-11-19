package co.electriccoin.zcash.ui.screen.scan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.screen.scan.model.ScanResultState
import co.electriccoin.zcash.ui.screen.scan.model.ScanValidationState
import com.google.gson.Gson
import com.keystone.sdk.KeystoneSDK
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ScanKeystoneViewModel(
    // private val args: ScanNavigationArgs,
    // private val getSynchronizer: GetSynchronizerUseCase,
    // private val zip321ParseUriValidationUseCase: Zip321ParseUriValidationUseCase,
) : ViewModel() {
    val navigateBack = MutableSharedFlow<ScanResultState>()

    val navigateCommand = MutableSharedFlow<String>()

    var state = MutableStateFlow(ScanValidationState.NONE)

    private val mutex = Mutex()

    private var hasBeenScannedSuccessfully = false

    private val sdk = KeystoneSDK()

    @OptIn(ExperimentalStdlibApi::class)
    fun onScanned(result: String) =
        viewModelScope.launch {
            mutex.withLock {
                val decodedResult = sdk.decodeQR(result)
                Log.d("kkcina", "=========> progress: " + decodedResult.progress)

                try {
                    // if (decodedResult.ur == null) {
                    //     qrScanIntegrator.initiateScan()
                    //     return
                    // }
                    val accounts = sdk.parseMultiAccounts(decodedResult.ur!!)
                    Log.d("kkcina", "=========> progress: " + Gson().toJson(accounts).toString())
                } catch (err: Exception) {
                    if (decodedResult.ur != null) {
                        val ur = decodedResult.ur!!
                        val gson = Gson().toJson(UR(ur.type, ur.cborBytes.toHexString()))
                        Log.d("kkcina", "=========> progress: $gson")
                    }
                    // Toast.makeText(binding.root.context, err.message, Toast.LENGTH_LONG).show()
                }

                // if (!hasBeenScannedSuccessfully) {
                //     val addressValidationResult = getSynchronizer().validateAddress(result)
                //
                //     val zip321ValidationResult = zip321ParseUriValidationUseCase(result)
                //
                //     state.update {
                //         if (addressValidationResult is AddressType.Valid) {
                //             ScanValidationState.INVALID
                //         } else if (zip321ValidationResult is Zip321ParseUriValidation.Valid) {
                //             ScanValidationState.INVALID
                //         } else {
                //             ScanValidationState.NONE
                //         }
                //     }
                //
                //     if (zip321ValidationResult is Zip321ParseUriValidation.Valid) {
                //         hasBeenScannedSuccessfully = true
                //         navigateBack.emit(ScanResultState.Zip321Uri(zip321ValidationResult.zip321Uri))
                //     } else if (addressValidationResult is AddressType.Valid) {
                //         hasBeenScannedSuccessfully = true
                //
                //         val serializableAddress = SerializableAddress(result, addressValidationResult)
                //
                //         when (args) {
                //             DEFAULT -> {
                //                 navigateBack.emit(
                //                     ScanResultState.Address(
                //                         Json.encodeToString(
                //                             SerializableAddress.serializer(),
                //                             serializableAddress
                //                         )
                //                     )
                //                 )
                //             }
                //
                //             ADDRESS_BOOK -> {
                //                 navigateCommand.emit(AddContactArgs(serializableAddress.address))
                //             }
                //         }
                //     }
                // }
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

data class UR(
    val type: String,
    val cbor: String,
)

