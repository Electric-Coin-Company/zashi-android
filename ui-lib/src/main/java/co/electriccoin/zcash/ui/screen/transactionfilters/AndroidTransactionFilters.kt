package co.electriccoin.zcash.ui.screen.transactionfilters

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.transactionfilters.view.TransactionFiltersView
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidTransactionFiltersList() {
    val viewModel = koinViewModel<TransactionFiltersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TransactionFiltersView(state = state)
}
