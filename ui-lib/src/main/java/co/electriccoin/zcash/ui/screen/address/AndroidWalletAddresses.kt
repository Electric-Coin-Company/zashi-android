@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.address

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.address.view.WalletAddresses
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel

@Composable
internal fun MainActivity.WrapWalletAddresses(
    goBack: () -> Unit
) {
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
        // Display loading indicator
    } else {
        WalletAddresses(
            walletAddresses,
            goBack,
            onCopyToClipboard = { copyToClipboard(activity.applicationContext, it) },
        )
    }
}

internal fun copyToClipboard(context: Context, address: String) {
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    val data = ClipData.newPlainText(
        context.getString(R.string.wallet_address_clipboard_tag),
        address
    )
    clipboardManager.setPrimaryClip(data)
}
