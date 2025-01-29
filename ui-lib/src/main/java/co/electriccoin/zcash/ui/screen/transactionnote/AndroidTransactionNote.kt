package co.electriccoin.zcash.ui.screen.transactionnote

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.screen.transactionnote.view.TransactionNoteView
import co.electriccoin.zcash.ui.screen.transactionnote.viewmodel.TransactionNoteViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidTransactionNote(transactionNote: TransactionNote) {
    val viewModel = koinViewModel<TransactionNoteViewModel> { parametersOf(transactionNote) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val parent = LocalView.current.parent

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    TransactionNoteView(
        state = state,
        sheetState = sheetState,
        onDismissRequest = state.onBack
    )

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    LaunchedEffect(Unit) {
        viewModel.hideBottomSheetRequest.collect {
            sheetState.hide()
            state.onBottomSheetHidden()
        }
    }

    BackHandler {
        state.onBack()
    }
}
