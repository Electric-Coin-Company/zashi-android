package co.electriccoin.zcash.ui.screen.restoresuccess

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.screen.restoresuccess.view.RestoreSuccess
import co.electriccoin.zcash.ui.screen.restoresuccess.viewmodel.RestoreSuccessViewModel

@Composable
fun WrapRestoreSuccess(onDone: () -> Unit) {
    val activity = LocalActivity.current

    val viewModel by activity.viewModels<RestoreSuccessViewModel>()

    val state = viewModel.state.collectAsStateWithLifecycle().value

    RestoreSuccess(
        state =
            state.copy(
                onPositiveClick = {
                    state.onPositiveClick()
                    onDone()
                }
            )
    )
}
