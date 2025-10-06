@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.feedback

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FeedbackScreen() {
    val vm = koinViewModel<FeedbackVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    val dialogState by vm.dialogState.collectAsStateWithLifecycle()
    BackHandler(enabled = state != null) { state?.onBack?.invoke() }
    state?.let { FeedbackView(state = it) }
    dialogState?.let { AppAlertDialog(state = it) }
}

@Serializable
data object FeedbackArgs
