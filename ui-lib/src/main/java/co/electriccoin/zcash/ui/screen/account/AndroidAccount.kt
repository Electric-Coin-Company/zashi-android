@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.account

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.viewmodel.CheckUpdateViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.account.view.Account
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImp
import co.electriccoin.zcash.ui.screen.update.model.UpdateState

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapAccount(
    goSettings: () -> Unit,
    goReceive: () -> Unit,
    goSend: () -> Unit,
    goHistory: () -> Unit
) {
    WrapAccount(
        this,
        goSettings = goSettings,
        goReceive = goReceive,
        goSend = goSend,
        goHistory = goHistory,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapAccount(
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
    val updateAvailable =
        checkUpdateViewModel.updateInfo.collectAsStateWithLifecycle().value.let {
            it?.appUpdateInfo != null && it.state == UpdateState.Prepared
        }

    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val settingsViewModel by activity.viewModels<SettingsViewModel>()

    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value
    val isFiatConversionEnabled = ConfigurationEntries.IS_FIAT_CONVERSION_ENABLED.getValue(RemoteConfig.current)

    if (null == walletSnapshot) {
        // Display loading indicator
    } else {
        Account(
            walletSnapshot = walletSnapshot,
            isUpdateAvailable = updateAvailable,
            isKeepScreenOnDuringSync = isKeepScreenOnWhileSyncing,
            isFiatConversionEnabled = isFiatConversionEnabled,
            goSettings = goSettings,
            goReceive = goReceive,
            goSend = goSend,
            goHistory = goHistory
        )

        activity.reportFullyDrawn()
    }
}
