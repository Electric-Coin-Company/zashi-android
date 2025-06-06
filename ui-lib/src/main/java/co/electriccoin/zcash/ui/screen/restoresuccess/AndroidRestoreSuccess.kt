package co.electriccoin.zcash.ui.screen.restoresuccess

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel

@Composable
fun WrapRestoreSuccess(onComplete: () -> Unit) {
    val viewModel = koinActivityViewModel<RestoreSuccessViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    RestoreSuccessView(
        state =
            state.copy(
                onPositiveClick = {
                    state.onPositiveClick()
                    onComplete()
                }
            )
    )
}
