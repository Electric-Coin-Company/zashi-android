package co.electriccoin.zcash.ui.screen.deletewallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun ResetZashiConfirmationScreen(args: ResetZashiConfirmationArgs) {
    val vm = koinViewModel<ResetZashiConfirmationVM> { parametersOf(args) }
    val bottomSheetState by vm.state.collectAsStateWithLifecycle()
    ResetZashiConfirmationView(bottomSheetState)
}

@Serializable
data class ResetZashiConfirmationArgs(
    val keepFiles: Boolean
)
