package co.electriccoin.zcash.ui.screen.error

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidErrorDialog() {
    val useCase = koinInject<NavigateToErrorUseCase>()
    val vm = koinViewModel<ErrorVM> { parametersOf(useCase.requireCurrentArgs()) }
    val state by vm.state.collectAsStateWithLifecycle()
    DialogView(state)
}

@Serializable
data object ErrorDialog
