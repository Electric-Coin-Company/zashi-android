@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.receive.view.Receive

@Composable
internal fun MainActivity.WrapReceive(
    onSettings: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    WrapReceive(
        this,
        onSettings = onSettings,
        onAddressDetails = onAddressDetails,
    )
}

@Composable
internal fun WrapReceive(
    activity: ComponentActivity,
    onSettings: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    val viewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = viewModel.addresses.collectAsStateWithLifecycle().value

    WrapReceive(
        walletAddresses,
        onSettings = onSettings,
        onAddressDetails = onAddressDetails,
    )
}

@Composable
internal fun WrapReceive(
    walletAddresses: WalletAddresses?,
    onSettings: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    if (null == walletAddresses) {
        // Improve this by allowing screen composition and updating it after the data is available
        CircularScreenProgressIndicator()
    } else {
        Receive(
            walletAddresses.unified,
            onSettings = onSettings,
            onAddressDetails = onAddressDetails,
            onAdjustBrightness = { /* Just for testing */ }
        )
    }
}
