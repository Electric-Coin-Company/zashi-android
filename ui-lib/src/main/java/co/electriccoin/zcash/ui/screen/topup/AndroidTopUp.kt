package co.electriccoin.zcash.ui.screen.topup

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.onLaunchUrl
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.topup.view.TopUp

@Composable
internal fun MainActivity.AndroidTopUp(onBack: () -> Unit) {
    WrapTopUp(
        activity = this,
        onBack = onBack,
        onLaunchUrl = {
            onLaunchUrl(it)
        }
    )
}

@Composable
internal fun WrapTopUp(activity: ComponentActivity, onBack: () -> Unit, onLaunchUrl: (String) -> Unit) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletAddress = walletViewModel.addresses.collectAsStateWithLifecycle().value
    TopUp(walletAddress = walletAddress, onBack = onBack, onLaunchUrl = { onLaunchUrl(it) })
}
