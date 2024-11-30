package co.electriccoin.zcash.ui.screen.accountlist

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.screen.accountlist.view.AccountListView
import co.electriccoin.zcash.ui.screen.accountlist.viewmodel.AccountListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidAccountList() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val viewModel = koinViewModel<AccountListViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    BackHandler {
        scope.launch {
            sheetState.hide()
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationCommand.collect {
            sheetState.hide()
            navController.navigate(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.backNavigationCommand.collect {
            sheetState.hide()
            navController.popBackStack()
        }
    }

    state?.let {
        AccountListView(
            state = it,
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    navController.popBackStack()
                }
            }
        )

        LaunchedEffect(Unit) {
            sheetState.show()
        }
    }
}

object AccountListArgs {
    const val PATH = "ACCOUNT_LIST"
}
