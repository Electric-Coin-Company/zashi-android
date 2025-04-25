package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidTransactionHistory() {
    val viewModel = koinViewModel<TransactionHistoryViewModel>()
    val mainTopAppBarViewModel = koinActivityViewModel<ZashiTopAppBarViewModel>()
    val mainAppBarState by mainTopAppBarViewModel.state.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchState by viewModel.search.collectAsStateWithLifecycle()

    BackHandler {
        state.onBack()
    }

    TransactionHistoryView(
        state = state,
        search = searchState,
        mainAppBarState = mainAppBarState
    )
}
