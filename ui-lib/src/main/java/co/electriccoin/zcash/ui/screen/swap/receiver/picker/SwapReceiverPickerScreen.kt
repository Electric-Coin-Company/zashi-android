package co.electriccoin.zcash.ui.screen.swap.receiver.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapReceiverPickerScreen() {
    val vm = koinViewModel<SwapReceiverPickerViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapReceiverPickerView(state)
}

@Serializable
object SwapReceiverPicker
