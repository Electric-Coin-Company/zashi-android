@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
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
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinActivityViewModel<AdvancedSettingsViewModel>()

    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    WrapAdvancedSettings(
        goBack = goBack,
        goDeleteWallet = goDeleteWallet,
        goExportPrivateData = goExportPrivateData,
        goChooseServer = goChooseServer,
        goSeedRecovery = goSeedRecovery,
        topAppBarSubTitleState = walletState,
        state = state
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
    topAppBarSubTitleState: TopAppBarSubTitleState,
    state: AdvancedSettingsState
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
        topAppBarSubTitleState = topAppBarSubTitleState,
        isBuyWithCoinbaseVisible = state.isBuyWithCoinbaseVisible,
        onBuyWithCoinbase = state.onBuyWithCoinbase
    )
}

data class AdvancedSettingsState(
    val isBuyWithCoinbaseVisible: Boolean,
    val onBuyWithCoinbase: (Activity) -> Unit,
)
