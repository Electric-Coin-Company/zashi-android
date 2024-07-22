@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel

@Composable
internal fun WrapChooseServer() {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val viewModel by activity.viewModels<ChooseServerViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChooseServerView(
        state = state,
        onBack = { navController.popBackStack() },
        topAppBarSubTitleState = walletState,
    )
}
