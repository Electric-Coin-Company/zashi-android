package co.electriccoin.zcash.ui.screen.disconnected.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Server Disconnected")
@Composable
private fun PreviewServerDisconnected() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            ServerDisconnected(
                onChooseServer = {},
                onIgnore = {}
            )
        }
    }
}

@Composable
fun ServerDisconnected(
    onChooseServer: () -> Unit,
    onIgnore: () -> Unit,
) {
    ServerDisconnectedDialog(
        onChooseServer = onChooseServer,
        onIgnore = onIgnore,
    )
}

@Composable
fun ServerDisconnectedDialog(
    onChooseServer: () -> Unit,
    onIgnore: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(id = R.string.server_disconnected_dialog_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = stringResource(id = R.string.server_disconnected_dialog_message))
            }
        },
        confirmButtonText = stringResource(id = R.string.server_disconnected_dialog_switch_btn),
        onConfirmButtonClick = onChooseServer,
        dismissButtonText = stringResource(id = R.string.server_disconnected_dialog_ignore_btn),
        onDismissButtonClick = onIgnore,
    )
}
