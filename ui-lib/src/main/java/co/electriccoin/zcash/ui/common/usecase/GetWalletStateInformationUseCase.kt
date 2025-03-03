package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository

class GetWalletStateInformationUseCase(
    private val walletRepository: WalletRepository
) {
    fun observe() = walletRepository.walletStateInformation
}
