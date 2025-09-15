package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidReviewTransaction() {
    val vm = koinViewModel<ReviewTransactionVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state?.onBack?.invoke() }
    state?.let { ReviewTransactionView(it) }
}
