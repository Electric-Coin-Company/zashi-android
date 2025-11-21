package co.electriccoin.zcash.ui.screen.advancedsettings.debug.db

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object DebugDBArgs

@Composable
fun DebugDBScreen() {
    val vm = koinViewModel<DebugDBVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    DebugDBView(state = state)
}
