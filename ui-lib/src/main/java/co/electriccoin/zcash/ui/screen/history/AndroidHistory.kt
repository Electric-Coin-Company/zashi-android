package co.electriccoin.zcash.ui.screen.history

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.history.view.History
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel

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

    val transactionHistoryState =
        walletViewModel.transactionHistoryState.collectAsStateWithLifecycle().value

    Twig.info { "Current transaction history state: $transactionHistoryState" }

    History(
        transactionState = transactionHistoryState,
        goBack = goBack
    )
}
