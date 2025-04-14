package co.electriccoin.zcash.ui.screen.balances.action

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidBalanceAction() {
    val vm = koinViewModel<BalanceActionViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    BalanceActionView(state)
}

@Serializable
data object BalanceAction
