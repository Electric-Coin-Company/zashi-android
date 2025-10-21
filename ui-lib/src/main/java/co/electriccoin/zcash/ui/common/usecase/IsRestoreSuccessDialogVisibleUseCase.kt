package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.provider.IsKeepScreenOnDuringRestoreProvider
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.combine

class IsRestoreSuccessDialogVisibleUseCase(
    private val walletRepository: WalletRepository,
    private val isKeepScreenOnDuringRestoreProvider: IsKeepScreenOnDuringRestoreProvider
) {
    fun observe() =
        combine(
            walletRepository.walletRestoringState,
            isKeepScreenOnDuringRestoreProvider.observe()
        ) { walletRestoringState, isKeepScreenOnDuringRestore ->
            walletRestoringState == WalletRestoringState.RESTORING && isKeepScreenOnDuringRestore == null
        }
}
