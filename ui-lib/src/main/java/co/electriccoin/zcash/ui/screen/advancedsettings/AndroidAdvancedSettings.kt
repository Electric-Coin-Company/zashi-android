@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.advancedsettings.view.AdvancedSettings
import co.electriccoin.zcash.ui.screen.advancedsettings.viewmodel.AdvancedSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Suppress("LongParameterList")
@Composable
internal fun WrapAdvancedSettings(
    goDeleteWallet: () -> Unit,
    goExportPrivateData: () -> Unit,
    goSeedRecovery: () -> Unit,
) {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<AdvancedSettingsViewModel>()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value
    val state =
        viewModel.state.collectAsStateWithLifecycle().value.copy(
            onDeleteZashiClick = goDeleteWallet,
            onExportPrivateDataClick = goExportPrivateData,
            onRecoveryPhraseClick = goSeedRecovery
        )

    BackHandler {
        viewModel.onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.navigationCommand.collect {
            navController.navigate(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    AdvancedSettings(
        state = state,
        topAppBarSubTitleState = walletState,
    )
}
