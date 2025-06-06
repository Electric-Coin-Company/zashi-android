package co.electriccoin.zcash.ui.screen.walletbackup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun AndroidWalletBackup(args: WalletBackup) {
    val viewModel = koinViewModel<WalletBackupViewModel> { parametersOf(args) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state?.onBack?.invoke() }
    state?.let { WalletBackupView(state = it) }
}

@Serializable
data class WalletBackup(
    val isOpenedFromSeedBackupInfo: Boolean
)
