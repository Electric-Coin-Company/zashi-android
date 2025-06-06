package co.electriccoin.zcash.ui.screen.exchangerate.optin

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidExchangeRateOptIn() {
    val viewModel = koinViewModel<ExchangeRateOptInViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    ExchangeRateOptInView(state = state)
}

@Serializable
object ExchangeRateOptIn
