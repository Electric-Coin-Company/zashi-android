package co.electriccoin.zcash.ui.screen.swap.near

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.GetSwapModeUseCase
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun NearInfoScreen() {
    val navigationRouter = koinInject<NavigationRouter>()
    val mode by koinInject<GetSwapModeUseCase>().observe().collectAsStateWithLifecycle()
    val state = NearInfoState(
        mode = mode,
        onBack = { navigationRouter.back() }
    )
    NearInfoView(state)
}

@Serializable
data object NearInfoArgs
