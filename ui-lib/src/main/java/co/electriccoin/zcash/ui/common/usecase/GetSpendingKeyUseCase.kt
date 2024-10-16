package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetSpendingKeyUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() = walletRepository.spendingKey.filterNotNull().first()
}
