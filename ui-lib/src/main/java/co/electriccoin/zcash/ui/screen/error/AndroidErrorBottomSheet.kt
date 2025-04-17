package co.electriccoin.zcash.ui.screen.error

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.usecase.NavigateToErrorUseCase
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidErrorBottomSheet() {
    val useCase = koinInject<NavigateToErrorUseCase>()
    val vm = koinViewModel<ErrorViewModel> { parametersOf(useCase.requireCurrentArgs()) }
    val state by vm.state.collectAsStateWithLifecycle()
    BottomSheetErrorView(state)
}

@Serializable
data object ErrorBottomSheet
