package co.electriccoin.zcash.ui.screen.restore.estimation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidRestoreBDEstimation() {
    val vm = koinViewModel<RestoreBDEstimationViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    RestoreBDEstimationView(state)
    BackHandler { state.onBack() }
}

@Serializable
data object RestoreBDEstimation
