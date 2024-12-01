package co.electriccoin.zcash.ui.screen.scankeystone

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.popBackStackJustOnce
import co.electriccoin.zcash.ui.screen.scankeystone.view.ScanKeystoneView
import co.electriccoin.zcash.ui.screen.scankeystone.viewmodel.ScanKeystoneSignInRequestViewModel
import co.electriccoin.zcash.ui.util.SettingsUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapScanKeystoneSignInRequestViewModel() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<ScanKeystoneSignInRequestViewModel>()
    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        navController.popBackStackJustOnce(ScanKeystoneNavigationArgs.PATH)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationCommand.collect {
            navController.navigate(it)
        }
    }

    if (synchronizer == null) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        ScanKeystoneView(
            snackbarHostState = snackbarHostState,
            validationResult = state,
            onBack = { navController.popBackStackJustOnce(ScanKeystoneNavigationArgs.PATH) },
            onScanned = {
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
            onScanStateChanged = {},
            topAppBarSubTitleState = walletState,
        )
    }
}

object ScanKeystoneNavigationArgs {
    const val PATH = "scan_keystone"
}