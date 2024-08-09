package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository

class ObserveSynchronizerUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.synchronizer
}
