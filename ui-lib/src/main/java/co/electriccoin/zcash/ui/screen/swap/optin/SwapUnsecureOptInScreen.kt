package co.electriccoin.zcash.ui.screen.swap.optin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapUnsecureOptInScreen() {
    val vm = koinViewModel<SwapUnsecureOptInVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapOptInView(state = state)
}

@Serializable
data object SwapUnsecureOptInArgs
