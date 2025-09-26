package co.electriccoin.zcash.ui.screen.home.shieldfunds

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidShieldFundsInfo() {
    val vm = koinViewModel<ShieldFundsInfoVM>()
    val state by vm.state.collectAsStateWithLifecycle()
    ShieldFundsInfoView(state)
}

@Serializable
object ShieldFundsInfo
