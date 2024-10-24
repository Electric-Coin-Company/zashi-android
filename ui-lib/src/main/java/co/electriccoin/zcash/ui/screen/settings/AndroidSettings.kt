package co.electriccoin.zcash.ui.screen.settings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.settings.view.Settings
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapSettings() {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        settingsViewModel.navigationCommand.collect {
            navController.navigate(it)
        }
    }

    LaunchedEffect(Unit) {
        settingsViewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        settingsViewModel.onBack()
    }

    Settings(
        state = state,
        topAppBarSubTitleState = walletState,
    )
}
