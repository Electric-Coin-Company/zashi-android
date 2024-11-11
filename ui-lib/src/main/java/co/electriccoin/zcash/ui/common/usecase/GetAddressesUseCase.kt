package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull

class GetAddressesUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.addresses.filterNotNull()
}
