package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetPersistableWalletUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() =
        walletRepository.persistableWallet
            .filterNotNull()
            .first()
}
