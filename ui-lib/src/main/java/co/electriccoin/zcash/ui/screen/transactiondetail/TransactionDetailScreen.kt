package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TransactionDetailScreen(transactionDetailArgs: TransactionDetailArgs) {
    val vm: TransactionDetailVM = koinViewModel { parametersOf(transactionDetailArgs) }
    val mainTopAppBarVM = koinActivityViewModel<ZashiTopAppBarVM>()
    val mainAppBarState by mainTopAppBarVM.state.collectAsStateWithLifecycle()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onBack?.invoke() }
    state?.let { TransactionDetailView(state = it, mainAppBarState = mainAppBarState) }
}

@Serializable
data class TransactionDetailArgs(val transactionId: String)

