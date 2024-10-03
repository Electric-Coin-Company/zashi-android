package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository

class GetSynchronizerUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() = walletRepository.getSynchronizer()
}
