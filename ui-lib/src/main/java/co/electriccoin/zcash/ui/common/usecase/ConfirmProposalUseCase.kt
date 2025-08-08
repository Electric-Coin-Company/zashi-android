package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.LatestSwapAssetsProvider
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.repository.BiometricsCancelledException
import co.electriccoin.zcash.ui.common.repository.BiometricsFailureException
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.signkeystonetransaction.SignKeystoneTransaction
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressArgs

class ConfirmProposalUseCase(
    private val navigationRouter: NavigationRouter,
    private val accountDataSource: AccountDataSource,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val biometricRepository: BiometricRepository,
    private val swapRepository: SwapRepository,
    private val latestSwapAssetsProvider: LatestSwapAssetsProvider,
) {
    suspend operator fun invoke() {
        try {
            biometricRepository.requestBiometrics(
                request =
                    BiometricRequest(
                        message =
                            stringRes(
                                R.string.authentication_system_ui_subtitle,
                                stringRes(R.string.authentication_use_case_send_funds)
                            )
                    )
            )

            val selectedSwapAsset = swapRepository.selectedAsset.value
            if (selectedSwapAsset != null) {
                latestSwapAssetsProvider.add(
                    tokenTicker = selectedSwapAsset.tokenTicker,
                    chainTicker = selectedSwapAsset.chainTicker
                )
            }
            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> navigationRouter.forward(SignKeystoneTransaction)
                is ZashiAccount -> {
                    zashiProposalRepository.submitTransaction()
                    navigationRouter.forward(TransactionProgressArgs)
                }
            }
            swapRepository.clear()
        } catch (_: BiometricsFailureException) {
            // do nothing
        } catch (_: BiometricsCancelledException) {
            // do nothing
        } catch (_: IllegalStateException) {
            // do nothing
        }
    }
}
