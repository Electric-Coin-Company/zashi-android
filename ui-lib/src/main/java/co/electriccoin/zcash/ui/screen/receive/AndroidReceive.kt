@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.receive.view.ReceiveView
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel

@Composable
internal fun WrapReceive() {
    val navController = LocalNavController.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    val receiveViewModel = koinActivityViewModel<ReceiveViewModel>()
    val receiveState by receiveViewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        receiveViewModel.navigationCommand.collect {
            navController.navigate(it)
        }
    }

    ReceiveView(
        state = receiveState,
        topAppBarSubTitleState = walletState,
        snackbarHostState = snackbarHostState
    )
}
