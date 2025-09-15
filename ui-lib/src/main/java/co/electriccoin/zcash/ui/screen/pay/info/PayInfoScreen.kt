package co.electriccoin.zcash.ui.screen.pay.info

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.NavigationRouter
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun PayInfoScreen() {
    val navigationRouter = koinInject<NavigationRouter>()
    val state =
        PayInfoState(
            onBack = { navigationRouter.back() }
        )
    PayInfoView(state)
}

@Serializable
data object PayInfoArgs
