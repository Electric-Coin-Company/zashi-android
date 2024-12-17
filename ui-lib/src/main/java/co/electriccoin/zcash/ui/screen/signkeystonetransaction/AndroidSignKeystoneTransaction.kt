package co.electriccoin.zcash.ui.screen.signkeystonetransaction

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.view.SignKeystoneTransactionView
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.viewmodel.SignKeystoneTransactionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidSignKeystoneTransaction() {
    val viewModel = koinViewModel<SignKeystoneTransactionViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state?.onBack?.invoke()
    }

    state?.let {
        SignKeystoneTransactionView(it)
    }
}
