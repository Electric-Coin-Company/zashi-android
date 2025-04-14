@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import co.electriccoin.zcash.ui.screen.receive.view.ReceiveView
import co.electriccoin.zcash.ui.screen.receive.viewmodel.ReceiveViewModel

@Composable
internal fun AndroidReceive() {
    val receiveViewModel = koinActivityViewModel<ReceiveViewModel>()
    val state by receiveViewModel.state.collectAsStateWithLifecycle()
    val topAppBarViewModel = koinActivityViewModel<ZashiTopAppBarViewModel>()
    val appBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state.onBack()
    }

    ReceiveView(
        state = state,
        appBarState = appBarState,
    )
}
