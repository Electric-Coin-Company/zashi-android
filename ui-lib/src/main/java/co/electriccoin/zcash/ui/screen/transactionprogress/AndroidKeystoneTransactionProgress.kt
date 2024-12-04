package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidKeystoneTransactionProgress(args: KeystoneTransactionProgress) {
    val viewModel = koinViewModel<KeystoneTransactionProgressViewModel> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state?.onBack?.invoke()
    }

    state?.let {
        TransactionProgressView(it)
    }
}