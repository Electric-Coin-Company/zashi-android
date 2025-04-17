package co.electriccoin.zcash.ui.screen.integrations

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidIntegrations() {
    val viewModel = koinViewModel<IntegrationsViewModel> { parametersOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = state != null) { state?.onBack?.invoke() }
    state?.let { Integrations(state = it) }
}

@Serializable
data object Integrations
