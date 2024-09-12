package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class RescanBlockchainUseCase(
    private val walletCoordinator: WalletCoordinator,
    private val standardPreferenceProvider: StandardPreferenceProvider
) {
    suspend operator fun invoke() =
        withContext(Dispatchers.IO + NonCancellable) {
            walletCoordinator.rescanBlockchain()
            persistWalletRestoringState(WalletRestoringState.RESTORING)
        }

    private suspend fun persistWalletRestoringState(walletRestoringState: WalletRestoringState) {
        StandardPreferenceKeys.WALLET_RESTORING_STATE.putValue(
            standardPreferenceProvider(),
            walletRestoringState.toNumber()
        )
    }
}
