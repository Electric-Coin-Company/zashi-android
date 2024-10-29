@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.support

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.screen.support.view.FeedbackView
import co.electriccoin.zcash.ui.screen.support.viewmodel.FeedbackViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AndroidFeedback() {
    val navController = LocalNavController.current
    val walletViewModel = koinActivityViewModel<WalletViewModel>()
    val viewModel = koinViewModel<FeedbackViewModel>()

    val walletState by walletViewModel.walletStateInformation.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onBackNavigationCommand.collect {
            navController.popBackStack()
        }
    }

    BackHandler {
        state?.onBack?.invoke()
    }

    state?.let {
        FeedbackView(
            state = it,
            topAppBarSubTitleState = walletState
        )
    }

    dialogState?.let {
        AppAlertDialog(
            state = it
        )
    }
}
