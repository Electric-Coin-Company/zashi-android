package co.electriccoin.zcash.ui.screen.home.backup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidWalletBackupInfo() {
    val viewModel = koinViewModel<WalletBackupInfoViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    WalletBackupInfoView(state = state)
}

@Serializable
object SeedBackupInfo
