package co.electriccoin.zcash.ui.screen.scan.thirdparty

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidThirdPartyScan() {
    val vm = koinViewModel<ThirdPartyScanViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    ThirdPartyScanView(state)
}

@Serializable
object ThirdPartyScan
