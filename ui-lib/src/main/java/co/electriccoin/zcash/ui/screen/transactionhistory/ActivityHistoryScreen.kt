package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActivityHistoryScreen() {
    val vm = koinViewModel<ActivityHistoryVM>()
    val mainTopAppBarViewModel = koinActivityViewModel<ZashiTopAppBarVM>()
    val mainAppBarState by mainTopAppBarViewModel.state.collectAsStateWithLifecycle()
    val state by vm.state.collectAsStateWithLifecycle()
    val searchState by vm.search.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    ActivityHistoryView(
        state = state,
        search = searchState,
        mainAppBarState = mainAppBarState
    )
}

@Serializable
data object ActivityHistoryArgs
