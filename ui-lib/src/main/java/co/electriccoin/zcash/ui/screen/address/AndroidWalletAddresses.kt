@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.address

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.address.view.WalletAddresses

@Composable
internal fun MainActivity.WrapWalletAddresses(goBack: () -> Unit) {
    WrapWalletAddresses(this, goBack)
}

@Composable
private fun WrapWalletAddresses(
    activity: ComponentActivity,
    goBack: () -> Unit
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val walletAddresses = walletViewModel.addresses.collectAsStateWithLifecycle().value

    if (null == walletAddresses) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        WalletAddresses(
            walletAddresses,
            goBack,
            onCopyToClipboard = { address ->
                ClipboardManagerUtil.copyToClipboard(
                    activity.applicationContext,
                    activity.getString(R.string.wallet_address_clipboard_tag),
                    address
                )
            },
        )
    }
}
