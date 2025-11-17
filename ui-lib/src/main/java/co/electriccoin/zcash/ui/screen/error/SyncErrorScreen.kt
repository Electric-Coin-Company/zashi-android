@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.error

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SyncErrorScreen() {
    val navigateToErrorUseCase = koinInject<NavigateToErrorUseCase>()
    val vm =
        koinViewModel<SyncErrorVM> {
            parametersOf(navigateToErrorUseCase.requireCurrentArgs() as ErrorArgs.SyncError)
        }
    val state by vm.state.collectAsStateWithLifecycle()
    SyncErrorView(state = state)
}

@Serializable
data object SyncErrorArgs
