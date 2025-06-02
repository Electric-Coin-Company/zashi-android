package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapSlippageScreen() {
    val vm = koinViewModel<SwapSlippageViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapSlippageView(state)
}

@Serializable
object SwapSlippage
