package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

@Composable
fun SwapSlippageScreen(args: SwapSlippageArgs) {
    val vm = koinViewModel<SwapSlippageViewModel> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    SwapSlippageView(state)
}

@Serializable
data class SwapSlippageArgs(
    val fiatAmount: String?
)
