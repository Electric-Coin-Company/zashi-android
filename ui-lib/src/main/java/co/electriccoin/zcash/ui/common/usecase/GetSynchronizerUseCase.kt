package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.SdkSynchronizer
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class GetSynchronizerUseCase(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke() = walletRepository.getSynchronizer()

    suspend fun getSdkSynchronizer() = walletRepository.getSynchronizer() as? SdkSynchronizer
}
