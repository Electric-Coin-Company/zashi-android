package co.electriccoin.zcash.ui.screen.migration

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.migration.view.OldAppMigration
import co.electriccoin.zcash.ui.screen.migration.viewmodel.AppMigrationViewModel

@Composable
internal fun MainActivity.AndroidAppMigration() {
    WrapAppMigration(activity = this)
}

@Composable
internal fun WrapAppMigration(activity: ComponentActivity) {
    val appMigrationViewModel = viewModel<AppMigrationViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val appMigrationState = appMigrationViewModel.appMigrationState.collectAsStateWithLifecycle().value

    OldAppMigration(
        appMigrationState = appMigrationState,
        onStartMigration = { appMigrationViewModel.checkForOldAppMigration() },
        onExit = { activity.finish() },
        onDataRecovered = {
            walletViewModel.persistExistingWallet(it)
            appMigrationViewModel.clearOldData()
            walletViewModel.checkForOldAppMigration()
        }
    )
}
