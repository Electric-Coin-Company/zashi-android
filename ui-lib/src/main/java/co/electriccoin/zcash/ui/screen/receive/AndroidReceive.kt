@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.receive.view.Receive

@Composable
internal fun WrapReceive(onSettings: () -> Unit) {
    val activity = LocalActivity.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val walletAddresses = walletViewModel.addresses.collectAsStateWithLifecycle().value

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    val snackbarHostState = remember { SnackbarHostState() }

    val versionInfo = VersionInfo.new(activity.applicationContext)

    Receive(
        onAddrCopyToClipboard = { address ->
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.receive_clipboard_tag),
                address
            )
        },
        onQrCode = {
            Toast.makeText(activity, "Not implemented yet", Toast.LENGTH_SHORT).show()
        },
        onRequest = {
            Toast.makeText(activity, "Not implemented yet", Toast.LENGTH_SHORT).show()
        },
        onSettings = onSettings,
        snackbarHostState = snackbarHostState,
        topAppBarSubTitleState = walletState,
        versionInfo = versionInfo,
        walletAddresses = walletAddresses,
    )
}
