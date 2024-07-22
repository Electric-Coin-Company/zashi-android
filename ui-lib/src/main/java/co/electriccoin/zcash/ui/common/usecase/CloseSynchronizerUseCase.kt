package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class CloseSynchronizerUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.closeSynchronizer()
}
