package co.electriccoin.zcash.ui.screen.resync.date

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.SecureScreen
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDateView
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ResyncBDDateScreen(args: ResyncBDDateArgs) {
    val vm = koinViewModel<ResyncBDDateVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    SecureScreen()
    BackHandler(enabled = state != null) { state?.onBack?.invoke() }
    state?.let { RestoreBDDateView(it) }
}

@Serializable
data class ResyncBDDateArgs(val uuid: String)
