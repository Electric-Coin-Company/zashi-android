@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarViewModel
import kotlinx.serialization.Serializable

@Composable
internal fun ReceiveScreen() {
    val receiveVM = koinActivityViewModel<ReceiveVM>()
    val state by receiveVM.state.collectAsStateWithLifecycle()
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

@Serializable
data object ReceiveArgs
