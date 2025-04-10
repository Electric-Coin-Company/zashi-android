@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.feedback

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.screen.feedback.view.FeedbackView
import co.electriccoin.zcash.ui.screen.feedback.viewmodel.FeedbackViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WrapFeedback() {
    val navController = LocalNavController.current
    val viewModel = koinViewModel<FeedbackViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onBackNavigationCommand.collect {
            navController.popBackStack()
        }
    }
    BackHandler(enabled = state != null) { state?.onBack?.invoke() }
    state?.let { FeedbackView(state = it) }
    dialogState?.let { AppAlertDialog(state = it) }
}
