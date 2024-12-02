package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetTransparentAddressUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() = walletRepository.currentAddresses.filterNotNull().map { it.transparent }.first()
}
