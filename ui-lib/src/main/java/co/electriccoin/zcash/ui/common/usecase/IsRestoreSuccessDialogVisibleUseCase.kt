package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class IsRestoreSuccessDialogVisibleUseCase(
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val walletRepository: WalletRepository,
) {
    fun observe() =
        combine(
            walletRepository.walletRestoringState,
            flow {
                emitAll(StandardPreferenceKeys.IS_RESTORING_INITIAL_WARNING_SEEN.observe(standardPreferenceProvider()))
            }
        ) { walletRestoringState, isSeen ->
            walletRestoringState == WalletRestoringState.RESTORING && !isSeen
        }

    suspend fun setSeen() {
        StandardPreferenceKeys.IS_RESTORING_INITIAL_WARNING_SEEN
            .putValue(
                preferenceProvider = standardPreferenceProvider(),
                newValue = true
            )
    }
}
