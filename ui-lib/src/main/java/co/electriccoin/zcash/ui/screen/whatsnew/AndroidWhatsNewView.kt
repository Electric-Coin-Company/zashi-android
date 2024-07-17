package co.electriccoin.zcash.ui.screen.whatsnew

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.whatsnew.view.WhatsNewViewInternal
import co.electriccoin.zcash.ui.screen.whatsnew.viewmodel.WhatsNewViewModel

@Composable
fun AndroidWhatsNewView() {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val viewModel by activity.viewModels<WhatsNewViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()
    val state = viewModel.state.collectAsStateWithLifecycle().value ?: return

    BackHandler {
        navController.popBackStack()
    }

    WhatsNewViewInternal(
        state = state,
        walletState = walletState,
        onBack = { navController.popBackStack() }
    )
}
