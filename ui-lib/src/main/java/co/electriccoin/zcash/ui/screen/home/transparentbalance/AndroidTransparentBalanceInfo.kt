package co.electriccoin.zcash.ui.screen.home.transparentbalance

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidTransparentBalanceInfo() {
    val vm = koinViewModel<TransparentBalanceInfoViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    state?.let { TransparentBalanceInfoView(it) }
}

@Serializable
object TransparentBalanceInfo
