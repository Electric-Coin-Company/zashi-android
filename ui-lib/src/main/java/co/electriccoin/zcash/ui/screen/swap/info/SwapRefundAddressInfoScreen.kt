package co.electriccoin.zcash.ui.screen.swap.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.NavigationRouter
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SwapRefundAddressInfoScreen() {
    val vm = koinViewModel<SwapRefundAddressInfoVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    SwapRefundAddressInfoView(state)
}

@Serializable
data object SwapRefundAddressInfoArgs
