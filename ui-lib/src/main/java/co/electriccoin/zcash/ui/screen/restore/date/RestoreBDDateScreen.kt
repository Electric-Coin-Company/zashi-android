package co.electriccoin.zcash.ui.screen.restore.date

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.SecureScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RestoreBDDateScreen(args: RestoreBDDateArgs) {
    val vm = koinViewModel<RestoreBDDateVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    SecureScreen()
    BackHandler(enabled = state != null) { state?.onBack?.invoke() }
    state?.let { RestoreBDDateView(it) }
}

@Serializable
data class RestoreBDDateArgs(
    val seed: String
)
