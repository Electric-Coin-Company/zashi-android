package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.provider.WalletRestoringStateProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class RescanBlockchainUseCase(
    private val walletCoordinator: WalletCoordinator,
    private val walletRestoringStateProvider: WalletRestoringStateProvider,
) {
    suspend operator fun invoke() =
        withContext(Dispatchers.IO + NonCancellable) {
            walletCoordinator.rescanBlockchain()
            walletRestoringStateProvider.store(WalletRestoringState.RESTORING)
        }
}
