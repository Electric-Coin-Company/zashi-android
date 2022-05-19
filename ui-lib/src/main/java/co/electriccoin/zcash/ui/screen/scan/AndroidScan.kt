package co.electriccoin.zcash.ui.screen.scan

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.scan.util.SettingsUtil
import co.electriccoin.zcash.ui.screen.scan.view.Scan
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.WrapScan(
    goBack: () -> Unit,
    onScanDone: (result: String) -> Unit
) {
    WrapScan(this, onScanDone, goBack)
}

@Composable
fun WrapScan(
    activity: ComponentActivity,
    onScanDone: (result: String) -> Unit,
    goBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scan(
        snackbarHostState,
        onBack = goBack,
        onScanDone = onScanDone,
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
        }
    )
}
