package co.electriccoin.zcash.ui.screen.scan

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.global.DeepLinkUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.scan.util.SettingsUtil
import co.electriccoin.zcash.ui.screen.scan.view.Scan
import co.electriccoin.zcash.ui.screen.send.nighthawk.viewmodel.SendViewModel
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapScanValidator(
    onScanValid: (address: String) -> Unit,
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
    onScanValid: (address: String) -> Unit,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val sendViewModel by activity.viewModels<SendViewModel>()

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (synchronizer == null) {
        // Display loading indicator
    } else {
        Scan(
            snackbarHostState = snackbarHostState,
            onBack = goBack,
            onScanned = { result ->
                scope.launch {
                    val sendDeepLinkData = DeepLinkUtil.getSendDeepLinkData(result.toUri())
                    val address = sendDeepLinkData?.address ?: result
                    val isAddressValid = !synchronizer.validateAddress(address).isNotValid
                    if (isAddressValid) {
                        sendDeepLinkData?.let {
                            sendViewModel.updateReceiverAddress(it.address)
                            it.amount?.let { zatoshi -> sendViewModel.enteredZecFromDeepLink(Zatoshi(zatoshi).convertZatoshiToZecString()) }
                            it.memo?.let { memo -> sendViewModel.updateMemo(memo) }
                        }
                        onScanValid(result)
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
