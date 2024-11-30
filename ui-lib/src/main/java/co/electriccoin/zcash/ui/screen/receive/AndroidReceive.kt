@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.receive.view.ReceiveView
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel

@Composable
internal fun WrapReceive() {
    val navController = LocalNavController.current
    val receiveViewModel = koinActivityViewModel<ReceiveViewModel>()
    val receiveState by receiveViewModel.state.collectAsStateWithLifecycle()
    val topAppBarViewModel = koinActivityViewModel<ZashiMainTopAppBarViewModel>()
    val zashiMainTopAppBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        receiveViewModel.navigationCommand.collect {
            navController.navigate(it)
        }
    }

    ReceiveView(
        state = receiveState,
        zashiMainTopAppBarState = zashiMainTopAppBarState,
    )
}
