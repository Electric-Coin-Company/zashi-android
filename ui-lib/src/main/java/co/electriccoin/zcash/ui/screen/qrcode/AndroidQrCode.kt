package co.electriccoin.zcash.ui.screen.qrcode

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.qrcode.view.QrCodeView
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapQrCode(addressType: Int) {
    val context = LocalContext.current
    val qrCodeViewModel = koinViewModel<QrCodeViewModel> { parametersOf(addressType) }
    val qrCodeState by qrCodeViewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        qrCodeViewModel.shareResultCommand.collect { sharedSuccessfully ->
            if (!sharedSuccessfully) {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.qr_code_data_unable_to_share)
                )
            }
        }
    }

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
