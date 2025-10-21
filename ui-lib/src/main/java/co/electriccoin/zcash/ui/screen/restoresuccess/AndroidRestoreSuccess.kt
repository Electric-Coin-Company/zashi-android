package co.electriccoin.zcash.ui.screen.restoresuccess

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel

@Composable
fun WrapRestoreSuccess() {
    val viewModel = koinActivityViewModel<RestoreSuccessViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    RestoreSuccessView(state)
}
