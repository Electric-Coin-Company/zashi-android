package co.electriccoin.zcash.ui.screen.swap.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Composable
fun SwapBlockchainPickerScreen(args: SwapBlockchainPickerArgs) {
    val vm = koinViewModel<SwapBlockchainPickerVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    SwapAssetPickerView(state)
}

@Serializable
data class SwapBlockchainPickerArgs(
    val requestId: String = UUID.randomUUID().toString()
)
