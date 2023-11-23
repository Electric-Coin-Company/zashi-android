@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel

@Composable
internal fun MainActivity.WrapSettings(
    goAbout: () -> Unit,
    goBack: () -> Unit,
    goExportPrivateData: () -> Unit,
    goSeedRecovery: () -> Unit,
) {
    WrapSettings(
        activity = this,
        goAbout = goAbout,
        goBack = goBack,
        goExportPrivateData = goExportPrivateData,
        goSeedRecovery = goSeedRecovery
    )
}

@Composable
private fun WrapSettings(
    activity: ComponentActivity,
    goBack: () -> Unit,
    goAbout: () -> Unit,
    goExportPrivateData: () -> Unit,
    goSeedRecovery: () -> Unit,
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val settingsViewModel by activity.viewModels<SettingsViewModel>()

    val versionInfo = VersionInfo.new(activity.applicationContext)

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
    val isBackgroundSyncEnabled = settingsViewModel.isBackgroundSync.collectAsStateWithLifecycle().value
    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value
    val isAnalyticsEnabled = settingsViewModel.isAnalyticsEnabled.collectAsStateWithLifecycle().value

    @Suppress("ComplexCondition")
    if (null == synchronizer ||
        null == isAnalyticsEnabled ||
        null == isBackgroundSyncEnabled ||
        null == isKeepScreenOnWhileSyncing
    ) {
        // Display loading indicator
    } else {
        Settings(
            TroubleshootingParameters(
                isEnabled = versionInfo.isDebuggable,
                isBackgroundSyncEnabled = isBackgroundSyncEnabled,
                isKeepScreenOnDuringSyncEnabled = isKeepScreenOnWhileSyncing,
                isAnalyticsEnabled = isAnalyticsEnabled,
                isRescanEnabled = ConfigurationEntries.IS_RESCAN_ENABLED.getValue(RemoteConfig.current),
            ),
            onBack = goBack,
            onSeedRecovery = goSeedRecovery,
            onDocumentation = {},
            onPrivacyPolicy = {},
            onFeedback = {},
            onAbout = goAbout,
            onExportPrivateData = goExportPrivateData,
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
            }
        )
    }
}
