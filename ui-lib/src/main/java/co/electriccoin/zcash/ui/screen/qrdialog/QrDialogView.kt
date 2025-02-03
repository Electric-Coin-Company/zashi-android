package co.electriccoin.zcash.ui.screen.qrdialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.electriccoin.zcash.ui.design.component.QrCodeDefaults
import co.electriccoin.zcash.ui.design.component.QrState
import co.electriccoin.zcash.ui.design.component.ZashiQr
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
fun QrDialogView(
    state: QrState,
    onBack: () -> Unit
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack
                )
                .padding(start = 16.dp, end = 16.dp, bottom = 64.dp)
    ) {
        ZashiQr(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            state = state,
            qrSize = LocalConfiguration.current.screenWidthDp.dp - 44.dp,
            contentPadding = PaddingValues(6.dp),
            colors =
                QrCodeDefaults.colors(
                    background = Color.White,
                    foreground = Color.Black,
                    border = Color.Unspecified
                )
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        Dialog(
            onDismissRequest = {}
        ) {
            QrDialogView(
                state =
                    QrState(
                        qrData = "test",
                        centerImageResId = co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone_qr
                    ),
                onBack = {}
            )
        }
    }
