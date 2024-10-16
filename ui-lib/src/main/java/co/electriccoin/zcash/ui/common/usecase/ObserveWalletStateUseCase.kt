package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository

class ObserveWalletStateUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.walletStateInformation
}
