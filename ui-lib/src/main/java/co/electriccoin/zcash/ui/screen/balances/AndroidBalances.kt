@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.balances

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.balances.view.Balances

@Composable
internal fun WrapBalances(
    activity: ComponentActivity,
    goSettings: () -> Unit,
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    Balances(
        walletSnapshot = walletSnapshot,
        onSettings = goSettings,
    )
}
