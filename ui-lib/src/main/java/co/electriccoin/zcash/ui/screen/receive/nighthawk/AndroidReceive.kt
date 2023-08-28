package co.electriccoin.zcash.ui.screen.receive.nighthawk

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.receive.nighthawk.view.ReceiveView

@Composable
internal fun MainActivity.AndroidReceive(
    onBack: () -> Unit,
    onShowQrCode: () -> Unit,
    onTopUpWallet: () -> Unit
) {
    WrapReceive(this, onBack = onBack, onShowQrCode = onShowQrCode, onTopUpWallet = onTopUpWallet)
}

@Composable
internal fun WrapReceive(
    activity: ComponentActivity,
    onBack: () -> Unit,
    onShowQrCode: () -> Unit,
    onTopUpWallet: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = walletViewModel.addresses.collectAsStateWithLifecycle().value
    ReceiveView(
        onBack = onBack,
        onShowQrCode = onShowQrCode,
        onCopyPrivateAddress = {
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.ns_private_address),
                walletAddresses?.sapling?.address ?: ""
            )
        },
        onTopUpWallet = onTopUpWallet,
        onCopyTransparentAddress = {
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.ns_transparent_address),
                walletAddresses?.transparent?.address ?: ""
            )
        }
    )
}
