package co.electriccoin.zcash.ui.screen.swap.info

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.NavigationRouter
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun SwapInfoScreen() {
    val navigationRouter = koinInject<NavigationRouter>()
    val state =
        SwapInfoState(
            onBack = { navigationRouter.back() }
        )
    SwapInfoInfoView(state)
}

@Serializable
data object SwapInfoArgs
