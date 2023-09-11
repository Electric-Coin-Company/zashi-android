@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel

@Composable
internal fun MainActivity.WrapSettings(
    goBack: () -> Unit
) {
    WrapSettings(
        activity = this,
        goBack = goBack,
    )
}

@Composable
private fun WrapSettings(
    activity: ComponentActivity,
    goBack: () -> Unit,
) {
    val settingsViewModel by activity.viewModels<SettingsViewModel>()

    val isKeepScreenOnWhileSyncing = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value
    if (
        null == isKeepScreenOnWhileSyncing
    ) {
        // Display loading indicator
    } else {
        Settings(
            onBack = goBack,
        )
    }
}
