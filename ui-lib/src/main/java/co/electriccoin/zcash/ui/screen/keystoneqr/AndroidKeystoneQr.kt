package co.electriccoin.zcash.ui.screen.keystoneqr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.keystoneqr.view.KeystoneQrView
import co.electriccoin.zcash.ui.screen.keystoneqr.viewmodel.KeystoneQrViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidKeystoneQr() {
    val viewModel = koinViewModel<KeystoneQrViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    state?.let {
        KeystoneQrView(it)
    }
}

object KeystoneQrNavigationArgs {
    const val PATH = "keystone_qr"
}
