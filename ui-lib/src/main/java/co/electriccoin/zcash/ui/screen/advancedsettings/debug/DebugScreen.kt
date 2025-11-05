package co.electriccoin.zcash.ui.screen.advancedsettings.debug

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun DebugScreen() {
    val vm = koinViewModel<DebugVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    DebugView(state)
}

@Serializable
data object DebugArgs
