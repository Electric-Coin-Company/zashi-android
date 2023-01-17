@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.profile

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.profile.view.Profile

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapProfile(
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
    onAddressBook: () -> Unit,
    onSettings: () -> Unit,
    onCoinholderVote: () -> Unit,
    onSupport: () -> Unit,
    onAbout: () -> Unit
) {
    WrapProfile(
        this,
        onBack = onBack,
        onAddressDetails = onAddressDetails,
        onAddressBook = onAddressBook,
        onSettings = onSettings,
        onCoinholderVote = onCoinholderVote,
        onSupport = onSupport,
        onAbout = onAbout
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapProfile(
    activity: ComponentActivity,
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
    onAddressBook: () -> Unit,
    onSettings: () -> Unit,
    onCoinholderVote: () -> Unit,
    onSupport: () -> Unit,
    onAbout: () -> Unit
) {
    val viewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = viewModel.addresses.collectAsStateWithLifecycle().value

    WrapProfile(
        walletAddresses,
        onBack = onBack,
        onAddressDetails = onAddressDetails,
        onAddressBook = onAddressBook,
        onSettings = onSettings,
        onCoinholderVote = onCoinholderVote,
        onSupport = onSupport,
        onAbout = onAbout
    )
}

@Composable
@Suppress("LongParameterList")
internal fun WrapProfile(
    walletAddresses: WalletAddresses?,
    onBack: () -> Unit,
    onAddressDetails: () -> Unit,
    onAddressBook: () -> Unit,
    onSettings: () -> Unit,
    onCoinholderVote: () -> Unit,
    onSupport: () -> Unit,
    onAbout: () -> Unit
) {
    if (null == walletAddresses) {
        // Display loading indicator
    } else {
        Profile(
            walletAddresses.unified,
            onBack = onBack,
            onAddressDetails = onAddressDetails,
            onAddressBook = onAddressBook,
            onSettings = onSettings,
            onCoinholderVote = onCoinholderVote,
            onSupport = onSupport,
            onAbout = onAbout
        )
    }
}
