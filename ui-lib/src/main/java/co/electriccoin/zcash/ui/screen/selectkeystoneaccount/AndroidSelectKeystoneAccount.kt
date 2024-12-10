package co.electriccoin.zcash.ui.screen.selectkeystoneaccount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.view.SelectKeystoneAccountView
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.viewmodel.SelectKeystoneAccountViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidSelectKeystoneAccount(args: SelectKeystoneAccount) {
    val viewModel = koinViewModel<SelectKeystoneAccountViewModel> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    state?.let {
        SelectKeystoneAccountView(state = it)
    }
}
