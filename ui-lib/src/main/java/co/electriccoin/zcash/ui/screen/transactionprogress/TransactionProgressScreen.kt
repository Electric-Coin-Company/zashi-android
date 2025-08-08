package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TransactionProgressScreen(args: TransactionProgressArgs) {
    val vm = koinViewModel<TransactionProgressVM> { parametersOf(args) }
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler { state?.onBack?.invoke() }
    state?.let { TransactionProgressView(it) }
}

@Serializable
data object TransactionProgressArgs
