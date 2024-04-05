@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel

@Composable
internal fun MainActivity.WrapSettings(
    goAbout: () -> Unit,
    goAdvancedSettings: () -> Unit,
    goBack: () -> Unit,
    goFeedback: () -> Unit,
) {
    val walletViewModel by viewModels<WalletViewModel>()

    val settingsViewModel by viewModels<SettingsViewModel>()

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapSettings(
        activity = this,
        goAbout = goAbout,
        goAdvancedSettings = goAdvancedSettings,
        goBack = goBack,
        goFeedback = goFeedback,
        settingsViewModel = settingsViewModel,
        walletViewModel = walletViewModel,
        walletRestoringState = walletRestoringState
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapSettings(
    activity: ComponentActivity,
    goAbout: () -> Unit,
    goAdvancedSettings: () -> Unit,
    goBack: () -> Unit,
    goFeedback: () -> Unit,
    settingsViewModel: SettingsViewModel,
    walletViewModel: WalletViewModel,
    walletRestoringState: WalletRestoringState,
) {
    val isBackgroundSyncEnabled = settingsViewModel.isBackgroundSync.collectAsStateWithLifecycle().value
    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value
    val isAnalyticsEnabled = settingsViewModel.isAnalyticsEnabled.collectAsStateWithLifecycle().value

    val versionInfo = VersionInfo.new(activity.applicationContext)

    BackHandler {
        goBack()
    }

    if (null == isAnalyticsEnabled ||
        null == isBackgroundSyncEnabled ||
        null == isKeepScreenOnWhileSyncing
    ) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Settings(
            onAbout = goAbout,
            onAdvancedSettings = goAdvancedSettings,
            onBack = goBack,
            onFeedback = goFeedback,
            troubleshootingParameters =
                TroubleshootingParameters(
                    isEnabled = versionInfo.isDebuggable && !versionInfo.isRunningUnderTestService,
                    isBackgroundSyncEnabled = isBackgroundSyncEnabled,
                    isKeepScreenOnDuringSyncEnabled = isKeepScreenOnWhileSyncing,
                    isAnalyticsEnabled = isAnalyticsEnabled,
                    isRescanEnabled = ConfigurationEntries.IS_RESCAN_ENABLED.getValue(RemoteConfig.current),
                ),
            onRescanWallet = {
                walletViewModel.rescanBlockchain()
            },
            onBackgroundSyncSettingsChanged = {
                settingsViewModel.setBackgroundSyncEnabled(it)
            },
            onKeepScreenOnDuringSyncSettingsChanged = {
                settingsViewModel.setKeepScreenOnWhileSyncing(it)
            },
            onAnalyticsSettingsChanged = {
                settingsViewModel.setAnalyticsEnabled(it)
            },
            walletRestoringState = walletRestoringState
        )
    }
}
