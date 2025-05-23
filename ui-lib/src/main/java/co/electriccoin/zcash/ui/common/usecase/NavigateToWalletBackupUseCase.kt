package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.repository.BiometricsCancelledException
import co.electriccoin.zcash.ui.common.repository.BiometricsFailureException
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.walletbackup.WalletBackup

class NavigateToWalletBackupUseCase(
    private val navigationRouter: NavigationRouter,
    private val biometricRepository: BiometricRepository
) {
    suspend operator fun invoke(isOpenedFromSeedBackupInfo: Boolean) {
        try {
            biometricRepository.requestBiometrics(
                BiometricRequest(
                    message =
                        stringRes(
                            R.string.authentication_system_ui_subtitle,
                            stringRes(R.string.authentication_use_case_seed_recovery)
                        )
                )
            )
            navigationRouter.forward(WalletBackup(isOpenedFromSeedBackupInfo = isOpenedFromSeedBackupInfo))
        } catch (_: BiometricsFailureException) {
            // do nothing
        } catch (_: BiometricsCancelledException) {
            // do nothing
        }
    }
}
