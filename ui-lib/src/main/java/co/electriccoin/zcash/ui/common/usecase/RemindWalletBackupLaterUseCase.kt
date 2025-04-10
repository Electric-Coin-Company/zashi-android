package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource

class RemindWalletBackupLaterUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletBackupDataSource: WalletBackupDataSource
) {
    suspend operator fun invoke() {
        walletBackupDataSource.remindMeLater()
        navigationRouter.backToRoot()
    }
}