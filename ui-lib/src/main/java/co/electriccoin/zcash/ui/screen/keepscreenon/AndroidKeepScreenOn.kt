package co.electriccoin.zcash.ui.screen.keepscreenon

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.electriccoin.zcash.ui.screen.keepscreenon.view.KeepScreenOn
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel

@Composable
internal fun AndroidKeepScreenOn(onBack: () -> Unit) {
    WrapKeepScreenOn(onBack = onBack)
}

@Composable
internal fun WrapKeepScreenOn(onBack: () -> Unit) {
    val settingsViewModel = viewModel<SettingsViewModel>()
    val isScreenOnEnabled = settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value
    KeepScreenOn(
        isScreenOnEnabled = isScreenOnEnabled,
        onScreenOnEnabledChanged =
        settingsViewModel::setKeepScreenOnWhileSyncing,
        onBack = onBack
    )
}
