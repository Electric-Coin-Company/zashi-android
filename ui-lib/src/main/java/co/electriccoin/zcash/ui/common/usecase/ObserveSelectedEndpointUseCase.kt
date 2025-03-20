package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveSelectedEndpointUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() =
        walletRepository.persistableWallet
            .map {
                it?.endpoint
            }.distinctUntilChanged()
}
