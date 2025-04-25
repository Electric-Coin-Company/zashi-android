package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.PersistableWalletStorageProvider
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow

class RescanQrUseCase(
    private val persistableWalletStorageProvider: PersistableWalletStorageProvider,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke() {
        if (persistableWalletStorageProvider.get() != null) {
            navigationRouter.replace(Scan(flow = ScanFlow.HOMEPAGE))
        } else {
            navigationRouter.back()
        }
    }
}
