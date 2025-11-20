package co.electriccoin.zcash.ui.screen.restore.estimation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.SecureScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RestoreBDEstimationScreen(args: RestoreBDEstimationArgs) {
    val vm = koinViewModel<RestoreBDEstimationVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    SecureScreen()
    BackHandler { state.onBack() }
    RestoreBDEstimationView(state)
}

@Serializable
data class RestoreBDEstimationArgs(val seed: String, val blockHeight: Long)
