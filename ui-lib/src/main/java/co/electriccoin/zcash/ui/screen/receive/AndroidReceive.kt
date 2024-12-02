@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.receive.view.ReceiveView
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel

@Composable
internal fun WrapReceive() {
    val receiveViewModel = koinActivityViewModel<ReceiveViewModel>()
    val receiveState by receiveViewModel.state.collectAsStateWithLifecycle()
    val topAppBarViewModel = koinActivityViewModel<ZashiMainTopAppBarViewModel>()
    val zashiMainTopAppBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ReceiveView(
        state = receiveState,
        snackbarHostState = snackbarHostState,
        zashiMainTopAppBarState = zashiMainTopAppBarState,
    )
}
