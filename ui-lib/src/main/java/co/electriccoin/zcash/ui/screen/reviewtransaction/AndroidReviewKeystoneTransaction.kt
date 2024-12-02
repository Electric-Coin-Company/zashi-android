package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidReviewKeystoneTransaction(args: ReviewKeystoneTransaction) {
    val viewModel = koinViewModel<ReviewKeystoneTransactionViewModel> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        state?.onBack
    }

    state?.let {
        ReviewTransactionView(it)
    }
}

