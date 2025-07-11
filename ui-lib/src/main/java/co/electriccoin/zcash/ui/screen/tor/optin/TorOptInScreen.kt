package co.electriccoin.zcash.ui.screen.tor.optin

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun TorOptInScreen() {
    val vm = koinViewModel<TorOptInVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    TorOptInView(state = state)
}

@Serializable
object TorOptInArgs
