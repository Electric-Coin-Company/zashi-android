package co.electriccoin.zcash.ui.screen.integrations

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationsScreen() {
    val vm = koinViewModel<IntegrationsVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    IntegrationsDialogView(state)
}

@Serializable
data object IntegrationsArgs
