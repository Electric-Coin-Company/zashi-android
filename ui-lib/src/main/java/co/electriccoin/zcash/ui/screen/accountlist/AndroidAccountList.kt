package co.electriccoin.zcash.ui.screen.accountlist

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

        LaunchedEffect(Unit) {
            viewModel.hideBottomSheetRequest.collect {
                sheetState.hide()
                state?.onBottomSheetHidden?.invoke()
            }
        }

        BackHandler {
            state?.onBack?.invoke()
        }
    }
}

