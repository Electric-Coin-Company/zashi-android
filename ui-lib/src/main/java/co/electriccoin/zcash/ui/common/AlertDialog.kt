package co.electriccoin.zcash.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
fun AlertDialogPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            AlertDialog(title = "Dialog Title", desc = "Description for Dialog", confirmText = "OK", dismissText = "Cancel")
        }
    }
}

@Composable
fun AlertDialog(title: String, desc: String, confirmText: String, dismissText: String, onConfirm: () -> Unit = {}, onDismiss : () -> Unit = {}, onDismissRequest : () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Body(text = title, color = ZcashTheme.colors.surfaceEnd)
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            if (dismissText.isNotBlank()) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(dismissText)
                }
            }
        },
        text = {
            BodyMedium(text = desc, color = Color.White)
        }
    )
}
