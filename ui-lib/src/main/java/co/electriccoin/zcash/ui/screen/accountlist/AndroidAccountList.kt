package co.electriccoin.zcash.ui.screen.accountlist

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
import co.electriccoin.zcash.ui.screen.accountlist.view.AccountListView
import co.electriccoin.zcash.ui.screen.accountlist.viewmodel.AccountListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidAccountList() {
    val viewModel = koinViewModel<AccountListViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetManager = LocalSheetStateManager.current
    DisposableEffect(sheetState) {
        sheetManager.onSheetOpened(sheetState)
        onDispose {
            sheetManager.onSheetDisposed(sheetState)
        }
    }

    val parent = LocalView.current.parent

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    state?.let {
        AccountListView(
            state = it,
            sheetState = sheetState,
            onDismissRequest = {
                state?.onBack?.invoke()
            }
        )

        LaunchedEffect(Unit) {
            sheetState.show()
        }

        BackHandler {
            state?.onBack?.invoke()
        }
    }
}
