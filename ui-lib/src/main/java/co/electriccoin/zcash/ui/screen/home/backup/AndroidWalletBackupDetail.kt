package co.electriccoin.zcash.ui.screen.home.backup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AndroidWalletBackupDetail(args: WalletBackupDetail) {
    val viewModel = koinViewModel<WalletBackupDetailViewModel> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    WalletBackupDetailView(state = state)
}

@Serializable
data class WalletBackupDetail(
    val isOpenedFromSeedBackupInfo: Boolean
)
