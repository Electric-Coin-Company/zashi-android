package cash.z.ecc.ui.screen.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.ui.theme.ZcashTheme

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        Home(PersistableWalletFixture.new())
    }
}

@Composable
fun Home(@Suppress("UNUSED_PARAMETER") persistableWallet: PersistableWallet) {
    Surface {
        Column {
            // Placeholder
            Text("Welcome to your wallet")
        }
    }
}
