@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapSettings(
    goAbout: () -> Unit,
    goBack: () -> Unit,
    goDocumentation: () -> Unit,
    goExportPrivateData: () -> Unit,
    goFeedback: () -> Unit,
    goPrivacyPolicy: () -> Unit,
    goSeedRecovery: () -> Unit,
) {
    WrapSettings(
        activity = this,
        goAbout = goAbout,
        goBack = goBack,
        goDocumentation = goDocumentation,
        goExportPrivateData = goExportPrivateData,
        goFeedback = goFeedback,
        goPrivacyPolicy = goPrivacyPolicy,
        goSeedRecovery = goSeedRecovery
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapSettings(
    activity: ComponentActivity,
    goAbout: () -> Unit,
    goBack: () -> Unit,
    goDocumentation: () -> Unit,
    goExportPrivateData: () -> Unit,
    goFeedback: () -> Unit,
    goPrivacyPolicy: () -> Unit,
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
        // Improve this by allowing screen composition and updating it after the data is available
        CircularScreenProgressIndicator()
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
            onDocumentation = goDocumentation,
            onPrivacyPolicy = goPrivacyPolicy,
            onFeedback = goFeedback,
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
