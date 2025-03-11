package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider

class GetPersistableWalletUseCase(
    private val persistableWalletProvider: PersistableWalletProvider
) {
    suspend operator fun invoke() = persistableWalletProvider.getPersistableWallet()
}
