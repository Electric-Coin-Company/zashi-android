package co.electriccoin.zcash.ui.screen.scan

import android.content.Context
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.scan.util.SettingsUtil
import co.electriccoin.zcash.ui.screen.scan.view.Scan
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapScanValidator(
    onScanValid: (address: SerializableAddress) -> Unit,
    goBack: () -> Unit
) {
    val walletViewModel by viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapScan(
        context = this,
        onScanValid = onScanValid,
        goBack = goBack,
        synchronizer = synchronizer,
        walletRestoringState = walletRestoringState
    )
}

@Composable
fun WrapScan(
    context: Context,
    goBack: () -> Unit,
    onScanValid: (address: SerializableAddress) -> Unit,
    synchronizer: Synchronizer?,
    walletRestoringState: WalletRestoringState,
) {
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    var addressValidationResult by remember { mutableStateOf<AddressType?>(null) }

    if (synchronizer == null) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Scan(
            snackbarHostState = snackbarHostState,
            addressValidationResult = addressValidationResult,
            onBack = goBack,
            onScanned = { result ->
                scope.launch {
                    addressValidationResult = synchronizer.validateAddress(result)
                    val isAddressValid = addressValidationResult?.let { !it.isNotValid } ?: false
                    if (isAddressValid) {
                        onScanValid(SerializableAddress(result, addressValidationResult!!))
                    }
                }
            },
            onOpenSettings = {
                runCatching {
                    context.startActivity(SettingsUtil.newSettingsIntent(context.packageName))
                }.onFailure {
                    // This case should not really happen, as the Settings app should be available on every
                    // Android device, but we need to handle it somehow.
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.scan_settings_open_failed)
                        )
                    }
                }
            },
            onScanStateChanged = {},
            walletRestoringState = walletRestoringState,
        )
    }
}
