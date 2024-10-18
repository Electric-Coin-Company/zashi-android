@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.paymentrequest

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import co.electriccoin.zcash.ui.screen.paymentrequest.view.PaymentRequestView
import co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel.PaymentRequestViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapPaymentRequest(
    arguments: PaymentRequestArguments
) {
    val navController = LocalNavController.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    val paymentRequestViewModel = koinViewModel<PaymentRequestViewModel> { parametersOf(arguments) }
    val paymentRequestState by paymentRequestViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        paymentRequestViewModel.closeNavigationCommand.collect {
            navController.popBackStack()
        }
    }
    LaunchedEffect(Unit) {
        paymentRequestViewModel.sendParametersCommand.collect {
            //TODO
            //navController.navigate()
        }
    }

    BackHandler {
        when (paymentRequestState) {
            PaymentRequestState.Loading -> {}
            else -> paymentRequestViewModel.onClose()
        }
    }

    PaymentRequestView(
        state = paymentRequestState,
        topAppBarSubTitleState = walletState,
    )
}
