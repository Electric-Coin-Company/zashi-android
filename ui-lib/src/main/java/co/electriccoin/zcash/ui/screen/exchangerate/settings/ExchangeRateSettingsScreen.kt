package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExchangeRateSettingsScreen() {
    val viewModel = koinViewModel<ExchangeRateSettingsVM>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    ExchangeRateSettingsView(state = state)
}

@Serializable
data object ExchangeRateSettingsArgs
