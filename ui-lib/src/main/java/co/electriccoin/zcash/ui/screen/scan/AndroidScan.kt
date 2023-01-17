@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.scan

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.scan.util.SettingsUtil
import co.electriccoin.zcash.ui.screen.scan.view.Scan
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapScanValidator(
    onScanValid: (address: String) -> Unit,
    goBack: () -> Unit
) {
    WrapScanValidator(
        this,
        onScanValid = onScanValid,
        goBack = goBack
    )
}

@Composable
private fun WrapScanValidator(
    activity: ComponentActivity,
    onScanValid: (address: String) -> Unit,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    if (synchronizer == null) {
        // Display loading indicator
    } else {
        WrapScan(
            activity,
            onScanned = { result ->
                activity.lifecycleScope.launch {
                    val isAddressValid = !synchronizer.validateAddress(result).isNotValid
                    if (isAddressValid) {
                        onScanValid(result)
                    }
                }
            },
            goBack = goBack
        )
    }
}

@Composable
fun WrapScan(
    activity: ComponentActivity,
    onScanned: (result: String) -> Unit,
    goBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scan(
        snackbarHostState,
        onBack = goBack,
        onScanned = onScanned,
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
