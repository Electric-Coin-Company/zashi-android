package co.electriccoin.zcash.ui.screen.resync.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConfirmResyncScreen() {
    val vm = koinViewModel<ConfirmResyncVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    state?.let { ConfirmResyncView(it) }
}

@Serializable
data object ConfirmResyncArgs
