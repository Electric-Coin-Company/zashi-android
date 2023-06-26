package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.transactionhistory.view.TransactionHistory

@Composable
internal fun MainActivity.AndroidTransactionHistory(onBack: () -> Unit, onTransactionDetail: (Long) -> Unit) {
    WrapTransactionHistory(activity = this, onBack = onBack, onTransactionDetail = onTransactionDetail)
}

@Composable
internal fun WrapTransactionHistory(activity: ComponentActivity, onBack: () -> Unit, onTransactionDetail: (Long) -> Unit) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val homeViewModel by activity.viewModels<HomeViewModel>()
    DisposableEffect(key1 = Unit) {
        homeViewModel.onBottomNavBarVisibilityChanged(show = false)
        onDispose {
            homeViewModel.onBottomNavBarVisibilityChanged(show = true)
        }
    }
    val transactionSnapshot = walletViewModel.transactionSnapshot.collectAsStateWithLifecycle().value
    TransactionHistory(transactionSnapshot = transactionSnapshot, onBack = onBack, onTransactionDetail = onTransactionDetail)
}
