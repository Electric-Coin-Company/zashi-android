package co.electriccoin.zcash.ui.screen.exchangerate.optin

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import kotlinx.serialization.Serializable

@Composable
fun AndroidExchangeRateOptIn() {
    val activity = LocalActivity.current
    val walletViewModel by activity.viewModels<WalletViewModel>()

    BackHandler {
        walletViewModel.dismissOptInExchangeRateUsd()
    }

    ExchangeRateOptIn(
        onEnableClick = { walletViewModel.optInExchangeRateUsd(true) },
        onDismiss = { walletViewModel.dismissOptInExchangeRateUsd() },
        onSkipClick = { walletViewModel.onSkipClick() }
    )
}

@Serializable
object ExchangeRateOptIn
