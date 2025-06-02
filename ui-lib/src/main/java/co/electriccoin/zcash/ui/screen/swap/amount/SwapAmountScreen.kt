package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapAmountScreen() {
    val vm = koinViewModel<SwapAmountViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapAmountView(state)
}

@Serializable
object SwapAmount
