package co.electriccoin.zcash.ui.screen.restore.height

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidRestoreBDHeight(args: RestoreBDHeight) {
    val vm = koinViewModel<RestoreBDHeightViewModel> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    RestoreBDHeightView(state)
    BackHandler {
        state.onBack()
    }
}

@Serializable
data class RestoreBDHeight(val seed: String)
