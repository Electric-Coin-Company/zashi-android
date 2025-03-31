package co.electriccoin.zcash.ui.screen.integrations

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.design.LocalSheetStateManager
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidDialogIntegrations() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetManager = LocalSheetStateManager.current
    DisposableEffect(sheetState) {
        sheetManager.onSheetOpened(sheetState)
        onDispose {
            sheetManager.onSheetDisposed(sheetState)
        }
    }
    val parent = LocalView.current.parent
    val viewModel = koinViewModel<IntegrationsViewModel> { parametersOf(true) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(enabled = state != null) {
        state?.onBack?.invoke()
    }

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    state?.let {
        IntegrationsDialogView(
            state = it,
            sheetState = sheetState,
            onDismissRequest = {
                it.onBack()
            }
        )

        LaunchedEffect(Unit) {
            sheetState.show()
        }
    }
}

@Serializable
data object DialogIntegrations
