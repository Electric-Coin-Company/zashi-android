package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetPersistableWalletUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() =
        walletRepository.secretState
            .map {
                (it as? SecretState.Ready?)?.persistableWallet
            }
            .filterNotNull()
            .first()
}
