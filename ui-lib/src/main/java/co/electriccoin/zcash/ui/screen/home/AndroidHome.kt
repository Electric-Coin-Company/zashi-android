@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.closeDrawerMenu
import co.electriccoin.zcash.ui.screen.home.view.Home
import co.electriccoin.zcash.ui.screen.home.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapHome(
    goSeedPhrase: () -> Unit,
    goSettings: () -> Unit,
    goSupport: () -> Unit,
    goAbout: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
) {
    WrapHome(
        this,
        goSeedPhrase = goSeedPhrase,
        goSettings = goSettings,
        goSupport = goSupport,
        goAbout = goAbout,
        goReceive = goReceive,
        goSend = goSend,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapHome(
    activity: ComponentActivity,
    goSeedPhrase: () -> Unit,
    goSettings: () -> Unit,
    goSupport: () -> Unit,
    goAbout: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
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

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // override Android back navigation action to close drawer, if opened
        BackHandler(drawerState.isOpen) {
            drawerState.closeDrawerMenu(scope)
        }

        Home(
            walletSnapshot,
            transactionSnapshot,
            isUpdateAvailable = updateAvailable,
            isKeepScreenOnDuringSync = isKeepScreenOnWhileSyncing,
            isDebugMenuEnabled = isDebugMenuEnabled,
            goSeedPhrase = goSeedPhrase,
            goSettings = goSettings,
            goSupport = goSupport,
            goAbout = goAbout,
            goReceive = goReceive,
            goSend = goSend,
            resetSdk = {
                walletViewModel.resetSdk()
            }
        )

        activity.reportFullyDrawn()
    }
}
