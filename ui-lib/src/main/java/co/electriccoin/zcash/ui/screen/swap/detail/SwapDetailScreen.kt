package co.electriccoin.zcash.ui.screen.swap.detail

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SwapDetailScreen(args: SwapDetailArgs) {
    val vm = koinViewModel<SwapDetailVM> { parametersOf(args) }
    val topAppBarViewModel = koinActivityViewModel<ZashiTopAppBarVM>()
    val appBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state?.onBack() }
    state?.let { SwapDetailView(it, appBarState) }
}

@Serializable
data class SwapDetailArgs(
    val depositAddress: String
)
