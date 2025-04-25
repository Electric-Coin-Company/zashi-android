package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapSettings() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    BackHandler {
        settingsViewModel.onBack()
    }
    state?.let {
        Settings(
            state = it,
        )
    }
}
