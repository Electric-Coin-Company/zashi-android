package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class PersistWalletUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke(persistableWallet: PersistableWallet) {
        walletRepository.persistWallet(persistableWallet)
    }
}
