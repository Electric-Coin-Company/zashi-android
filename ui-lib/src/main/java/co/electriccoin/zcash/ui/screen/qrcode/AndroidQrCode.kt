package co.electriccoin.zcash.ui.screen.qrcode

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.qrcode.model.QrCodeState
import co.electriccoin.zcash.ui.screen.qrcode.view.QrCodeView
import co.electriccoin.zcash.ui.screen.qrcode.viewmodel.QrCodeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapQrCode(addressType: Int) {
    val navController = LocalNavController.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    val qrCodeViewModel = koinViewModel<QrCodeViewModel> { parametersOf(addressType) }
    val qrCodeState by qrCodeViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        qrCodeViewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        when (qrCodeState) {
            QrCodeState.Loading -> {}
            is QrCodeState.Prepared -> (qrCodeState as QrCodeState.Prepared).onBack.invoke()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    QrCodeView(
        state = qrCodeState,
        topAppBarSubTitleState = walletState,
        snackbarHostState = snackbarHostState
    )
}
