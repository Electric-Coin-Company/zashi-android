package co.electriccoin.zcash.ui.screen.swap.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwapRefundAddressInfoScreen() {
    val vm = koinViewModel<SwapRefundAddressInfoVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapRefundAddressInfoView(state)
}

@Serializable
data object SwapRefundAddressInfoArgs
