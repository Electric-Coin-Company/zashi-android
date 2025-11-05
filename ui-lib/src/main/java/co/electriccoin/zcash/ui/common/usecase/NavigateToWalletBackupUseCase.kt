package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.walletbackup.WalletBackup

class NavigateToWalletBackupUseCase(
    private val navigationRouter: NavigationRouter,
) {
    operator fun invoke(isOpenedFromSeedBackupInfo: Boolean) {
        navigationRouter.forward(WalletBackup(isOpenedFromSeedBackupInfo = isOpenedFromSeedBackupInfo))
    }
}
