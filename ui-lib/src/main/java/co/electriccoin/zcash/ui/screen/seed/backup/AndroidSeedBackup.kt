package co.electriccoin.zcash.ui.screen.seed.backup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import co.electriccoin.zcash.ui.screen.seed.SeedRecovery
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun AndroidSeedBackup() {
    val viewModel = koinActivityViewModel<WalletViewModel>()
    val appBarState by viewModel.walletStateInformation.collectAsStateWithLifecycle()
    val navigationRouter = koinInject<NavigationRouter>()
    val state = remember {
        SeedBackupState(
            onBack = { navigationRouter.back() },
            onNextClick = { navigationRouter.replace(SeedRecovery) },
            onInfoClick = { navigationRouter.forward(SeedInfo) }
        )
    }

    BackHandler {
        state.onBack()
    }

    SeedBackupView(
        state = state,
        appBarState = appBarState,
    )
}

@Serializable
object SeedBackup
