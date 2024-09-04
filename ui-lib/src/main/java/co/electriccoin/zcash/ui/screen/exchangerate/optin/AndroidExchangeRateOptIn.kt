package co.electriccoin.zcash.ui.screen.exchangerate.optin

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel

@Composable
fun AndroidExchangeRateOptIn() {
    val activity = LocalActivity.current
    val walletViewModel by activity.viewModels<WalletViewModel>()

    BackHandler {
        walletViewModel.dismissOptInExchangeRateUsd()
    }

    ExchangeRateOptIn(
        onEnabledClick = { walletViewModel.optInExchangeRateUsd(true) },
        onDismiss = { walletViewModel.dismissOptInExchangeRateUsd() }
    )
}
