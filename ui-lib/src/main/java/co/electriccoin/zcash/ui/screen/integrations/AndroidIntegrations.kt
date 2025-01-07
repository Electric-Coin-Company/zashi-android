package co.electriccoin.zcash.ui.screen.integrations

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.integrations.view.Integrations
import co.electriccoin.zcash.ui.screen.integrations.viewmodel.IntegrationsViewModel
import com.flexa.core.Flexa
import com.flexa.spend.buildSpend
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapIntegrations() {
    val activity = LocalActivity.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<IntegrationsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.flexaNavigationCommand.collect {
            Flexa.buildSpend()
                .onTransactionRequest {
                    viewModel.onFlexaResultCallback(it)
                }
                .build()
                .open(activity)
        }
    }

    BackHandler {
        viewModel.onBack()
    }

    state?.let {
        Integrations(
            state = it,
            topAppBarSubTitleState = walletState,
        )
    }
}
