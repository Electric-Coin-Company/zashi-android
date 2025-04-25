package co.electriccoin.zcash.ui.screen.transactionnote

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.transactionnote.view.TransactionNoteView
import co.electriccoin.zcash.ui.screen.transactionnote.viewmodel.TransactionNoteViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidTransactionNote(transactionNote: TransactionNote) {
    val viewModel = koinViewModel<TransactionNoteViewModel> { parametersOf(transactionNote) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    TransactionNoteView(state = state)
}
