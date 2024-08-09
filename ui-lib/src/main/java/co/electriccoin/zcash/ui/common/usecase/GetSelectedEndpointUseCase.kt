package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class GetSelectedEndpointUseCase(
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(): LightWalletEndpoint = walletRepository.getSelectedServer()
}
