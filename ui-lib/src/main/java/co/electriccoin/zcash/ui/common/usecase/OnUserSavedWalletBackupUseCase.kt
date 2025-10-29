package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.WalletBackupFlagStorageProvider

class OnUserSavedWalletBackupUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletBackupFlagStorageProvider: WalletBackupFlagStorageProvider
) {
    suspend operator fun invoke() {
        walletBackupFlagStorageProvider.store(true)
        navigationRouter.backToRoot()
    }
}
