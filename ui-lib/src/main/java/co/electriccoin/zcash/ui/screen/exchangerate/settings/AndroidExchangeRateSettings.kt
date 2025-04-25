package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidExchangeRateSettings() {
    val viewModel = koinViewModel<ExchangeRateSettingsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onDismiss() }
    ExchangeRateSettingsView(state = state)
}

@Serializable
object ExchangeRateSettings
