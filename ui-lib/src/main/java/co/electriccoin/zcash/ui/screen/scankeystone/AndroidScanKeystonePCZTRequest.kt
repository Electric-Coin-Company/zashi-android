package co.electriccoin.zcash.ui.screen.scankeystone

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.scankeystone.view.ScanKeystoneView
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystonePCZTViewModel
import co.electriccoin.zcash.ui.util.SettingsUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun WrapScanKeystonePCZTRequest() {
    val navigationRouter = koinInject<NavigationRouter>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<ScanKeystonePCZTViewModel>()
    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
    val validationState by viewModel.validationState.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        navigationRouter.back()
    }

    if (synchronizer == null) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        ScanKeystoneView(
            snackbarHostState = snackbarHostState,
            onBack = { navigationRouter.back() },
            onScan = {
                viewModel.onScanned(it)
            },
            onOpenSettings = {
                runCatching {
                    context.startActivity(SettingsUtil.newSettingsIntent(context.packageName))
                }.onFailure {
                    // This case should not really happen, as the Settings app should be available on every
                    // Android device, but rather handle it.
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.scan_settings_open_failed)
                        )
                    }
                }
            },
            onScanStateChange = {},
            validationResult = validationState,
            state = state,
        )
    }
}
