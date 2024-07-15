package cash.z.ecc.sdk.usecase

import cash.z.ecc.sdk.repository.WalletRepository

class ObserveSynchronizerUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke() = walletRepository.synchronizer
}
