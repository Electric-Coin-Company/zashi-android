package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidTransactionHistory() {
    val viewModel = koinViewModel<TransactionHistoryViewModel>()
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val mainTopAppBarViewModel = koinViewModel<ZashiMainTopAppBarViewModel>()
    val mainAppBarState by mainTopAppBarViewModel.state.collectAsStateWithLifecycle()
    val topAppbarState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.onScrollToTopRequested.collect {
            listState.scrollToItem(0)
        }
    }

    BackHandler {
        state.onBack()
    }

    TransactionHistoryView(
        state = state,
        mainAppBarState = mainAppBarState,
        appBarState = topAppbarState,
        listState = listState
    )
}
