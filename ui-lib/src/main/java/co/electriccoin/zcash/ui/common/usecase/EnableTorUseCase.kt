package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.IsTorExplicitlySetProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider

class EnableTorUseCase(
    private val persistableWalletProvider: PersistableWalletProvider,
    private val isTorExplicitlySetProvider: IsTorExplicitlySetProvider,
    private val navigationRouter: NavigationRouter,
) {
    suspend operator fun invoke(enable: Boolean) {
        val persistableWallet = persistableWalletProvider.getPersistableWallet() ?: return
        persistableWalletProvider.store(persistableWallet.copy(isTorEnabled = enable))
        isTorExplicitlySetProvider.store(true)
        navigationRouter.back()
    }
}
