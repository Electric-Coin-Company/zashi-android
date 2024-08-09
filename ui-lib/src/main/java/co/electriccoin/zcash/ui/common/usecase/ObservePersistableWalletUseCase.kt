package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

class ObservePersistableWalletUseCase(
    private val walletRepository: WalletRepository
) {
    operator fun invoke(): Flow<PersistableWallet?> = walletRepository.persistableWallet
}
