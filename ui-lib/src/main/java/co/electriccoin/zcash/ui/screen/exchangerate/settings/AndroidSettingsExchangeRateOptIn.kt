package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel

@Composable
fun AndroidSettingsExchangeRateOptIn() {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val isOptedIn = walletViewModel.isExchangeRateUsdOptedIn.collectAsStateWithLifecycle().value ?: false

    BackHandler {
        navController.popBackStack()
    }

    SettingsExchangeRateOptIn(
        isOptedIn = isOptedIn,
        onSaveClick = { walletViewModel.optInExchangeRateUsd(it) },
        onDismiss = { navController.popBackStack() }
    )
}
