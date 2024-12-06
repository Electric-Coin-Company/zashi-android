package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.PersistableWallet
import kotlinx.coroutines.flow.Flow

class ObservePersistableWalletUseCase(
    private val walletCoordinator: WalletCoordinator,
) {
    operator fun invoke(): Flow<PersistableWallet?> = walletCoordinator.persistableWallet
}
