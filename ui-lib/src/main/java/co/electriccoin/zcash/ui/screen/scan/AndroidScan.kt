package co.electriccoin.zcash.ui.screen.scan

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
import co.electriccoin.zcash.ui.NavigationArguments.SEND_SCAN_RECIPIENT_ADDRESS
import co.electriccoin.zcash.ui.NavigationArguments.SEND_SCAN_ZIP_321_URI
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.popBackStackJustOnce
import co.electriccoin.zcash.ui.screen.scan.model.ScanResultState
import co.electriccoin.zcash.ui.screen.scan.view.Scan
import co.electriccoin.zcash.ui.screen.scan.viewmodel.ScanViewModel
import co.electriccoin.zcash.ui.util.SettingsUtil
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapScanValidator(args: ScanNavigationArgs) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<ScanViewModel> { parametersOf(args) }
    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        navController.popBackStackJustOnce(ScanNavigationArgs.ROUTE)
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { scanResult ->
            navController.previousBackStackEntry?.savedStateHandle?.apply {
                when (scanResult) {
                    is ScanResultState.Address -> set(SEND_SCAN_RECIPIENT_ADDRESS, scanResult.address)
                    is ScanResultState.Zip321Uri -> set(SEND_SCAN_ZIP_321_URI, scanResult.zip321Uri)
                }
            }
            navController.popBackStackJustOnce(ScanNavigationArgs.ROUTE)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateCommand.collect {
            navController.popBackStack()
            navController.navigate(it)
        }
    }

    if (synchronizer == null) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Scan(
            snackbarHostState = snackbarHostState,
            validationResult = state,
            onBack = { navController.popBackStackJustOnce(ScanNavigationArgs.ROUTE) },
            onScanned = {
                viewModel.onScanned(it)
            },
            onScanError = {
                viewModel.onScannedError()
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

enum class ScanNavigationArgs {
    DEFAULT,
    ADDRESS_BOOK;

    companion object {
        private const val PATH = "scan"
        const val KEY = "mode"
        const val ROUTE = "$PATH/{$KEY}"

        operator fun invoke(mode: ScanNavigationArgs) = "$PATH/${mode.name}"
    }
}
