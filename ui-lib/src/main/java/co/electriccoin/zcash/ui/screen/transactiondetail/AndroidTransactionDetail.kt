package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidTransactionDetail(transactionDetail: TransactionDetail) {
    val viewModel: TransactionDetailViewModel = koinViewModel { parametersOf(transactionDetail) }
    val walletViewModel: WalletViewModel = koinViewModel()
    val mainTopAppBarViewModel = koinViewModel<ZashiTopAppBarViewModel>()
    val mainAppBarState by mainTopAppBarViewModel.state.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appBarState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    BackHandler(state != null) {
        state?.onBack?.invoke()
    }

    state?.let {
        TransactionDetailView(
            state = it,
            appBarState = appBarState,
            mainAppBarState = mainAppBarState
        )
    }
}
