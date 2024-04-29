@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.advancedsettings.view.AdvancedSettings

@Composable
internal fun MainActivity.WrapAdvancedSettings(
    goBack: () -> Unit,
    goDeleteWallet: () -> Unit,
    goExportPrivateData: () -> Unit,
    goChooseServer: () -> Unit,
    goSeedRecovery: () -> Unit,
) {
    val walletViewModel by viewModels<WalletViewModel>()

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    WrapAdvancedSettings(
        goBack = goBack,
        goDeleteWallet = goDeleteWallet,
        goExportPrivateData = goExportPrivateData,
        goChooseServer = goChooseServer,
        goSeedRecovery = goSeedRecovery,
        walletRestoringState = walletRestoringState,
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapAdvancedSettings(
    goBack: () -> Unit,
    goExportPrivateData: () -> Unit,
    goChooseServer: () -> Unit,
    goSeedRecovery: () -> Unit,
    goDeleteWallet: () -> Unit,
    walletRestoringState: WalletRestoringState,
) {
    BackHandler {
        goBack()
    }

    AdvancedSettings(
        onBack = goBack,
        onDeleteWallet = goDeleteWallet,
        onExportPrivateData = goExportPrivateData,
        onChooseServer = goChooseServer,
        onSeedRecovery = goSeedRecovery,
        walletRestoringState = walletRestoringState,
    )
}
