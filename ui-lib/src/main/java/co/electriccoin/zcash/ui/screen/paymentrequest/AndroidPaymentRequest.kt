@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.paymentrequest

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.paymentrequest.view.PaymentRequestView
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import co.electriccoin.zcash.ui.screen.request.viewmodel.RequestViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapPaymentRequest(addressType: Int) {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    val requestViewModel = koinViewModel<RequestViewModel> { parametersOf(addressType) }
    val requestState by requestViewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        requestViewModel.backNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        when (requestState) {
            RequestState.Loading -> {}
            else -> requestViewModel.onBack()
        }
    }

    PaymentRequestView(
        state = requestState,
        topAppBarSubTitleState = walletState,
        snackbarHostState = snackbarHostState
    )
}
