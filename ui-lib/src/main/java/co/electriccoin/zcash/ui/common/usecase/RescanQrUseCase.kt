package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow

class RescanQrUseCase(
    private val persistableWalletProvider: PersistableWalletProvider,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke() {
        if (persistableWalletProvider.getPersistableWallet() != null) {
            navigationRouter.replace(Scan(flow = ScanFlow.HOMEPAGE))
        } else {
            navigationRouter.back()
        }
    }
}
