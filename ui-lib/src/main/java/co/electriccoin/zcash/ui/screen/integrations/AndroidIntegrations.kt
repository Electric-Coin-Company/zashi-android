package co.electriccoin.zcash.ui.screen.integrations

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.integrations.view.Integrations
import co.electriccoin.zcash.ui.screen.integrations.viewmodel.IntegrationsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapIntegrations() {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<IntegrationsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.coinbaseNavigationCommand.collect { uri ->
            val intent =
                CustomTabsIntent.Builder()
                    .setUrlBarHidingEnabled(true)
                    .setShowTitle(true)
                    .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                    .build()
            intent.launchUrl(activity, Uri.parse(uri))
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
