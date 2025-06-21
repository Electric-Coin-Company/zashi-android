package co.electriccoin.zcash.ui.screen.swap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapScreen() {
    val vm = koinViewModel<SwapVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    state?.let { SwapView(it) }
}

@Serializable
object SwapAmount
