package co.electriccoin.zcash.ui.screen.seed

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AndroidSeedRecovery() {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value
    val viewModel = koinViewModel<SeedRecoveryViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        state?.onBack?.invoke()
    }

    state?.let {
        SeedRecoveryView(
            state = it,
            topAppBarSubTitleState = walletState,
        )
    }
}

@Serializable
object SeedRecovery
