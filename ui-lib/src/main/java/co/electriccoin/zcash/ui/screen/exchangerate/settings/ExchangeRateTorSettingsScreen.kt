package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExchangeRateTorSettingsScreen() {
    val vm = koinViewModel<ExchangeRateTorSettingsVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    ExchangeRateTorView(state)
}

@Serializable
data object ExchangeRateTorSettingsArgs
