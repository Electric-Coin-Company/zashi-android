package co.electriccoin.zcash.ui.screen.history

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.history.view.History
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MainActivity.WrapHistory(
    goBack: () -> Unit
) {
    WrapHistory(
        activity = this,
        goBack = goBack
    )
}

@Composable
internal fun WrapHistory(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val transactionHistory =
        walletViewModel.transactionHistory.collectAsStateWithLifecycle().value.toImmutableList()

    val isSyncing = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value?.status ==
        Synchronizer.Status.SYNCING

    History(
        transactions = transactionHistory,
        isSyncing = isSyncing,
        goBack = goBack
    )
}
