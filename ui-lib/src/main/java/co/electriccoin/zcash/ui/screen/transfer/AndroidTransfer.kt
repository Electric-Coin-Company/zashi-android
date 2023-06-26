package co.electriccoin.zcash.ui.screen.transfer

import androidx.compose.runtime.Composable
import androidx.core.app.ComponentActivity
import cash.z.ecc.android.sdk.internal.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.transfer.view.TransferMainView

@Composable
internal fun MainActivity.AndroidTransfer(onSendMoney: () -> Unit, onReceiveMoney: () -> Unit, onTopUp: () -> Unit) {
    WrapTransfer(activity = this, onSendMoney = onSendMoney, onReceiveMoney = onReceiveMoney, onTopUp = onTopUp)
}

@Composable
internal fun WrapTransfer(activity: ComponentActivity, onSendMoney: () -> Unit, onReceiveMoney: () -> Unit, onTopUp: () -> Unit) {
    Twig.info { "Just for initial run $activity" }
    TransferMainView(onSendMoney = onSendMoney, onReceiveMoney = onReceiveMoney, onTopUp = onTopUp)
}