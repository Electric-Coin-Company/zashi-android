package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository

class ObserveFastestServersUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.fastestEndpoints
}
