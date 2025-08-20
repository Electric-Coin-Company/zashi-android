package co.electriccoin.zcash.ui.screen.transactionfilters

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.transactionfilters.viewmodel.TransactionFiltersVM
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFiltersScreen() {
    val vm = koinViewModel<TransactionFiltersVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    BackHandler(state != null) { state?.onBack?.invoke() }
    TransactionFiltersView(state = state)
}

@Serializable
object TransactionFiltersArgs
