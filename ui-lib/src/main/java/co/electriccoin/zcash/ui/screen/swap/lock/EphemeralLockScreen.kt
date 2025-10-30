package co.electriccoin.zcash.ui.screen.swap.lock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun EphemeralLockScreen() {
    val vm = koinViewModel<EphemeralLockVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    EphemeralLockView(state)
}

@Serializable
data object EphemeralLockArgs
