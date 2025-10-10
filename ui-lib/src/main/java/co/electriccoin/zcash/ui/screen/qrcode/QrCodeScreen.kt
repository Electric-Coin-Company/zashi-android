package co.electriccoin.zcash.ui.screen.qrcode

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun QrCodeScreen(addressType: Int) {
    val vm = koinViewModel<QrCodeVM> { parametersOf(addressType) }
    val qrCodeState by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    BackHandler {
        when (val qrCodeState = qrCodeState) {
            QrCodeState.Loading -> {}
            is QrCodeState.Prepared -> qrCodeState.onBack.invoke()
        }
    }
    QrCodeView(
        state = qrCodeState,
        snackbarHostState = snackbarHostState
    )
}
