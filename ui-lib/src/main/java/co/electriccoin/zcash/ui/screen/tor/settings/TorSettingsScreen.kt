package co.electriccoin.zcash.ui.screen.tor.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun TorSettingsScreen() {
    val vm = koinViewModel<TorSettingsVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onDismiss?.invoke() }
    state?.let { TorSettingsView(state = it) }
}

@Serializable
object TorSettingsArgs
