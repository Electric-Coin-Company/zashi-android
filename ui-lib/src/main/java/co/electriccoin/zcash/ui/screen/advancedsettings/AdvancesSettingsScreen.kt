@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AdvancedSettingsScreen() {
    val viewModel = koinViewModel<AdvancedSettingsVM>()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    BackHandler { viewModel.onBack() }
    AdvancedSettings(state = state)
}

@Serializable
data object AdvancedSettingsArgs
