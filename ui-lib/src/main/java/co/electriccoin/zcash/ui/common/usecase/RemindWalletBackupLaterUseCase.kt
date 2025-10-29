package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.WalletBackupConsentStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeCountStorageProvider
import co.electriccoin.zcash.ui.common.provider.WalletBackupRemindMeTimestampStorageProvider
import java.time.Instant

class RemindWalletBackupLaterUseCase(
    private val navigationRouter: NavigationRouter,
    private val walletBackupConsentStorageProvider: WalletBackupConsentStorageProvider,
    private val walletBackupRemindMeCountStorageProvider: WalletBackupRemindMeCountStorageProvider,
    private val walletBackupRemindMeTimestampStorageProvider: WalletBackupRemindMeTimestampStorageProvider
) {
    suspend operator fun invoke(persistConsent: Boolean) {
        if (persistConsent) {
            walletBackupConsentStorageProvider.store(true)
        }
        remindMeLater()
        navigationRouter.backToRoot()
    }

    private suspend fun remindMeLater() {
        val count = walletBackupRemindMeCountStorageProvider.get()
        val timestamp = Instant.now()
        walletBackupRemindMeCountStorageProvider.store(count + 1)
        walletBackupRemindMeTimestampStorageProvider.store(timestamp)
    }
}
