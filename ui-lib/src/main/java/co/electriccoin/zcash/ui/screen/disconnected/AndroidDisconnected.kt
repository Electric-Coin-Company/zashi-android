@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.disconnected

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.disconnected.model.DisconnectedUiState
import co.electriccoin.zcash.ui.screen.disconnected.view.ServerDisconnected

@Composable
internal fun MainActivity.WrapDisconnected(
    goChooseServer: () -> Unit,
    onIgnore: () -> Unit,
) {
    co.electriccoin.zcash.ui.screen.disconnected.WrapDisconnected(
        goChooseServer = goChooseServer,
        onIgnore = onIgnore,
    )
}

@Composable
private fun WrapDisconnected(
    goChooseServer: () -> Unit,
    onIgnore: () -> Unit,
) {
    val (disconnectedUi, setDisconnectedUi) =
        rememberSaveable(stateSaver = DisconnectedUiState.Saver) { mutableStateOf(DisconnectedUiState.Displayed) }

    if (disconnectedUi == DisconnectedUiState.Displayed) {
        ServerDisconnected(
            onChooseServer = {
                setDisconnectedUi(DisconnectedUiState.Dismissed)
                goChooseServer()
            },
            onIgnore = {
                setDisconnectedUi(DisconnectedUiState.Dismissed)
                onIgnore()
            },
        )
    }
}
