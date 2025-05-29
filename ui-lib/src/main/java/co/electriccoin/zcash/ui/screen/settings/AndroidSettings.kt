package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapSettings() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    BackHandler(state?.onBack != null) { state?.onBack?.invoke() }
    state?.let { Settings(state = it) }
}
