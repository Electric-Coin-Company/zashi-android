package co.electriccoin.zcash.ui.screen.swap.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapAssetPickerScreen() {
    val vm = koinViewModel<SwapAssetPickerVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapAssetPickerView(state)
}

@Serializable
object SwapAssetPickerArgs
