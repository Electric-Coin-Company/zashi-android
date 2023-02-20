@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.home.view.Home
import co.electriccoin.zcash.ui.screen.home.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Composable
internal fun MainActivity.WrapHome(
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    WrapHome(
        this,
        goScan = goScan,
        goProfile = goProfile,
        goSend = goSend,
        goRequest = goRequest
    )
}

@Composable
internal fun WrapHome(
    activity: ComponentActivity,
    goScan: () -> Unit,
    goProfile: () -> Unit,
    goSend: () -> Unit,
    goRequest: () -> Unit
) {
    // we want to show information about app update, if available
    val checkUpdateViewModel by activity.viewModels<CheckUpdateViewModel> {
        CheckUpdateViewModel.CheckUpdateViewModelFactory(
            activity.application,
            AppUpdateCheckerImp.new()
        )
    }
    val updateAvailable = checkUpdateViewModel.updateInfo.collectAsStateWithLifecycle().value.let {
        it?.appUpdateInfo != null && it.state == UpdateState.Prepared
    }

    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val settingsViewModel by activity.viewModels<SettingsViewModel>()

    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value

    if (null == walletSnapshot) {
        // Display loading indicator
    } else {
        val context = LocalContext.current

        // We might eventually want to check the debuggable property of the manifest instead
        // of relying on BuildConfig.
        val isDebugMenuEnabled = BuildConfig.DEBUG &&
            !FirebaseTestLabUtil.isFirebaseTestLab(context) &&
            !EmulatorWtfUtil.isEmulatorWtf(context)

        val transactionSnapshot = walletViewModel.transactionSnapshot.collectAsStateWithLifecycle().value

        Home(
            walletSnapshot,
            isKeepScreenOnDuringSync = isKeepScreenOnWhileSyncing,
            isRequestZecButtonEnabled = ConfigurationEntries.IS_REQUEST_ZEC_ENABLED.getValue(RemoteConfig.current),
            transactionSnapshot,
            goScan = goScan,
            goRequest = goRequest,
            goSend = goSend,
            goProfile = goProfile,
            isDebugMenuEnabled = isDebugMenuEnabled,
            resetSdk = {
                walletViewModel.resetSdk()
            },
            updateAvailable = updateAvailable
        )

        activity.reportFullyDrawn()
    }
}
