package co.electriccoin.zcash.ui.screen.balances.spendable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendableBalanceScreen() {
    val vm = koinViewModel<SpendableBalanceVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    SpendableBalanceView(state)
}

@Serializable
data object SpendableBalanceArgs
