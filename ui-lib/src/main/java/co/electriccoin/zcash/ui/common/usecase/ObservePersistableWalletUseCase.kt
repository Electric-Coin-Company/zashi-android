package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import kotlinx.coroutines.flow.map

class ObservePersistableWalletUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.secretState.map {
        (it as? SecretState.Ready?)?.persistableWallet
    }
}
