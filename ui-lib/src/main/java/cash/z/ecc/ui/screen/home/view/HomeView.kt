package cash.z.ecc.ui.screen.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.ui.theme.ZcashTheme

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        Home()
    }
}

@Composable
fun Home() {
    Surface {
        Column {
            // Placeholder
            Text("Welcome to your wallet")
        }
    }
}
