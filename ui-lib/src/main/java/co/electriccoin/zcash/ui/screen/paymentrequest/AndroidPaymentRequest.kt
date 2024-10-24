@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.paymentrequest

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.Proposal
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.NavigationTargets.HOME
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.authentication.AuthenticationUseCase
import co.electriccoin.zcash.ui.screen.authentication.WrapAuthentication
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestStage
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestState
import co.electriccoin.zcash.ui.screen.paymentrequest.view.PaymentRequestView
import co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel.PaymentRequestViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapPaymentRequest(arguments: PaymentRequestArguments) {
    val activity = LocalActivity.current as MainActivity
    val navController = LocalNavController.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()

    val paymentRequestViewModel = koinViewModel<PaymentRequestViewModel> { parametersOf(arguments) }
    val paymentRequestState by paymentRequestViewModel.state.collectAsStateWithLifecycle()

    val authenticateForProposal = rememberSaveable { mutableStateOf<Proposal?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val onBackAction = {
        when (paymentRequestState) {
            PaymentRequestState.Loading -> {}
            is PaymentRequestState.Prepared -> {
                val state = (paymentRequestState as PaymentRequestState.Prepared)
                when (state.stage) {
                    PaymentRequestStage.Initial,
                    PaymentRequestStage.Confirmed -> navController.popBackStack()
                    PaymentRequestStage.Sending -> {
                        // No action - wait until the sending is done
                    }
                    is PaymentRequestStage.Failure -> paymentRequestViewModel.setStage(PaymentRequestStage.Initial)
                    is PaymentRequestStage.FailureGrpc -> {
                        paymentRequestViewModel.setStage(PaymentRequestStage.Confirmed)
                        navController.navigate(HOME)
                    }
                }
            }
        }
    }

    BackHandler { onBackAction() }

    LaunchedEffect(Unit) {
        paymentRequestViewModel.backNavigationCommand.collect {
            onBackAction()
        }
    }
    LaunchedEffect(Unit) {
        paymentRequestViewModel.closeNavigationCommand.collect {
            navController.popBackStack()
        }
    }
    LaunchedEffect(Unit) {
        paymentRequestViewModel.addContactNavigationCommand.collect {
            navController.navigate(AddContactArgs(it))
        }
    }
    LaunchedEffect(Unit) {
        paymentRequestViewModel.authenticationNavigationCommand.collect {
            authenticateForProposal.value = it
        }
    }
    LaunchedEffect(Unit) {
        paymentRequestViewModel.homeNavigationCommand.collect {
            navController.navigate(HOME)
        }
    }
    LaunchedEffect(Unit) {
        paymentRequestViewModel.sendReportFailedNavigationCommand.collect {
            snackbarHostState.showSnackbar(
                message = activity.getString(R.string.payment_request_send_failed_report_unable_open_email)
            )
        }
    }

    PaymentRequestView(
        state = paymentRequestState,
        topAppBarSubTitleState = walletState,
        snackbarHostState = snackbarHostState
    )

    if (authenticateForProposal.value != null) {
        activity.WrapAuthentication(
            goSupport = {
                authenticateForProposal.value = null
                navController.navigate(NavigationTargets.SUPPORT)
            },
            onSuccess = {
                paymentRequestViewModel.onSendAllowed(authenticateForProposal.value!!)
                authenticateForProposal.value = null
            },
            onCancel = {
                authenticateForProposal.value = null
            },
            onFailed = {
                // No action needed
            },
            useCase = AuthenticationUseCase.SendFunds
        )
    }
}
