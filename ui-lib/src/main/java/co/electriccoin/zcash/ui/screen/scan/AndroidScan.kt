package co.electriccoin.zcash.ui.screen.scan

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SerializableAddress
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
    WrapScan(
        this,
        onScanValid = onScanValid,
        goBack = goBack
    )
}

@Composable
fun WrapScan(
    activity: ComponentActivity,
    onScanValid: (address: SerializableAddress) -> Unit,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (synchronizer == null) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Scan(
            snackbarHostState = snackbarHostState,
            onBack = goBack,
            onScanned = { result ->
                scope.launch {
                    val addressType = synchronizer.validateAddress(result)
                    val isAddressValid = !addressType.isNotValid
                    if (isAddressValid) {
                        onScanValid(SerializableAddress(result, addressType))
                    } else {
                        snackbarHostState.showSnackbar(
                            message = activity.getString(R.string.scan_validation_invalid_address)
                        )
                    }
                }
            },
            onOpenSettings = {
                runCatching {
                    activity.startActivity(SettingsUtil.newSettingsIntent(activity.packageName))
                }.onFailure {
                    // This case should not really happen, as the Settings app should be available on every
                    // Android device, but we need to handle it somehow.
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = activity.getString(R.string.scan_settings_open_failed)
                        )
                    }
                }
            },
            onScanStateChanged = {}
        )
    }
}
