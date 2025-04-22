package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource
import co.electriccoin.zcash.ui.common.provider.WalletBackupConsentStorageProvider

class RemindWalletBackupLaterUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletBackupDataSource: WalletBackupDataSource,
    private val walletBackupConsentStorageProvider: WalletBackupConsentStorageProvider
) {
    suspend operator fun invoke(persistConsent: Boolean) {
        if (persistConsent) {
            walletBackupConsentStorageProvider.store(true)
        }
        walletBackupDataSource.remindMeLater()
        navigationRouter.backToRoot()
    }
}
