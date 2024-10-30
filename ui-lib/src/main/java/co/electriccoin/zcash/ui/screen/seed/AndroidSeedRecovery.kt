package co.electriccoin.zcash.ui.screen.seed

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.seed.view.SeedView
import co.electriccoin.zcash.ui.screen.seed.viewmodel.SeedViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapSeed(
    args: SeedNavigationArgs,
    goBackOverride: (() -> Unit)?
) {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value
    val viewModel = koinViewModel<SeedViewModel> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    val normalizedState =
        state?.copy(
            onBack =
                state?.onBack?.let {
                    {
                        goBackOverride?.invoke()
                        it.invoke()
                    }
                }
        )

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        normalizedState?.onBack?.invoke()
    }

    normalizedState?.let {
        SeedView(
            state = normalizedState,
            topAppBarSubTitleState = walletState,
        )
    }
}

enum class SeedNavigationArgs {
    NEW_WALLET,
    RECOVERY
}
