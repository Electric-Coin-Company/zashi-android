@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.closeDrawerMenu
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.home.view.Home
import co.electriccoin.zcash.ui.screen.home.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import kotlinx.coroutines.CoroutineScope

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapHome(
    goSettings: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit
) {
    WrapHome(
        this,
        goSettings = goSettings,
        goReceive = goReceive,
        goSend = goSend,
        goHistory = goHistory,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapHome(
    activity: ComponentActivity,
    goSettings: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit,
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
    val isFiatConversionEnabled = ConfigurationEntries.IS_FIAT_CONVERSION_ENABLED.getValue(RemoteConfig.current)
    val isCircularProgressBarEnabled =
        ConfigurationEntries.IS_HOME_CIRCULAR_PROGRESS_BAR_ENABLED.getValue(RemoteConfig.current)

    if (null == walletSnapshot) {
        // Display loading indicator
    } else {
        Home(
            walletSnapshot = walletSnapshot,
            isUpdateAvailable = updateAvailable,
            isKeepScreenOnDuringSync = isKeepScreenOnWhileSyncing,
            isFiatConversionEnabled = isFiatConversionEnabled,
            isCircularProgressBarEnabled = isCircularProgressBarEnabled,
            goSettings = goSettings,
            goReceive = goReceive,
            goSend = goSend,
            goHistory = goHistory
        )

        activity.reportFullyDrawn()
    }
}

/**
 * Custom Drawer menu composable with back navigation handling feature, which returns its necessary values.
 */
@Composable
internal fun drawerBackHandler(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    scope: CoroutineScope = rememberCoroutineScope()
): DrawerValuesWrapper {
    // Override Android back navigation action to close drawer, if opened
    BackHandler(drawerState.isOpen) {
        drawerState.closeDrawerMenu(scope)
    }
    return DrawerValuesWrapper(drawerState, scope)
}

internal data class DrawerValuesWrapper(
    val drawerState: DrawerState,
    val scope: CoroutineScope
)
