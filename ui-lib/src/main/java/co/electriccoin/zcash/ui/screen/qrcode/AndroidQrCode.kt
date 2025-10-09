package co.electriccoin.zcash.ui.screen.qrcode

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.qrcode.view.QrCodeView
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeVM
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapQrCode(addressType: Int) {
    val vm = koinViewModel<QrCodeVM> { parametersOf(addressType) }
    val qrCodeState by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    BackHandler {
        when (qrCodeState) {
            QrCodeState.Loading -> {}
            is QrCodeState.Prepared -> (qrCodeState as QrCodeState.Prepared).onBack.invoke()
        }
    }
    QrCodeView(
        state = qrCodeState,
        snackbarHostState = snackbarHostState
    )
}
