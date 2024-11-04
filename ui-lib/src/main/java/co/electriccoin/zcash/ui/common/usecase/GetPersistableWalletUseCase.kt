package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository

class GetPersistableWalletUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() = walletRepository.getPersistableWallet()
}
