package co.electriccoin.zcash.ui.screen.transactionfilters

import android.view.WindowManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.transactionfilters.view.TransactionFiltersView
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidTransactionFiltersList() {
    val viewModel = koinViewModel<TransactionFiltersViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val parent = LocalView.current.parent

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    TransactionFiltersView(
        state = state,
    )
}
