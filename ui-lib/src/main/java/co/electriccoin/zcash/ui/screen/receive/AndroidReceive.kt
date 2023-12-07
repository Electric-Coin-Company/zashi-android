@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.receive.view.Receive

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapReceive(
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    WrapReceive(
        this,
        onBack = onBack,
        onAddressDetails = onAddressDetails,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapReceive(
    activity: ComponentActivity,
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    val viewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = viewModel.addresses.collectAsStateWithLifecycle().value

    WrapReceive(
        walletAddresses,
        onBack = onBack,
        onAddressDetails = onAddressDetails,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapReceive(
    walletAddresses: WalletAddresses?,
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
) {
    if (null == walletAddresses) {
        // Display loading indicator
    } else {
        Receive(
            walletAddresses.unified,
            onBack = onBack,
            onAddressDetails = onAddressDetails,
            onAdjustBrightness = { /* Just for testing */ }
        )
    }
}
