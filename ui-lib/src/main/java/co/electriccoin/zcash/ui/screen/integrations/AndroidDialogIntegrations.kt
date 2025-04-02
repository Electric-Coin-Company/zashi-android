package co.electriccoin.zcash.ui.screen.integrations

import android.view.WindowManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidDialogIntegrations() {
    val parent = LocalView.current.parent
    val viewModel = koinViewModel<IntegrationsViewModel> { parametersOf(true) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    state?.let {
        IntegrationsDialogView(
            state = it,
        )
    }
}

@Serializable
data object DialogIntegrations
