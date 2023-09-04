@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.screen.settings

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.settings.view.Settings

@Composable
internal fun MainActivity.WrapSettings(
    goBack: () -> Unit
) {
    WrapSettings(
        goBack = goBack,
    )
}

@Composable
private fun WrapSettings(
    goBack: () -> Unit,
) {
    Settings(
        //  isRescanEnabled = ConfigurationEntries.IS_RESCAN_ENABLED.getValue(RemoteConfig.current),
        onBack = goBack,
        /* onRescanWallet = {
             walletViewModel.rescanBlockchain()
         },*/
    )
}
