package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource

class OnUserSavedWalletBackupUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletBackupDataSource: WalletBackupDataSource
) {
    suspend operator fun invoke() {
        walletBackupDataSource.onUserSavedWalletBackup()
        navigationRouter.backToRoot()
    }
}
