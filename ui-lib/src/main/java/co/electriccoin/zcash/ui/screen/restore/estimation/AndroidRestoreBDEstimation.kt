package co.electriccoin.zcash.ui.screen.restore.estimation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidRestoreBDEstimation(args: RestoreBDEstimation) {
    val vm = koinViewModel<RestoreBDEstimationViewModel> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    RestoreBDEstimationView(state)
    BackHandler { state.onBack() }
}

@Serializable
data class RestoreBDEstimation(
    val seed: String,
    val blockHeight: Long
)
